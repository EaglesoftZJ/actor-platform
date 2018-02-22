/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.runtime.generic.mvvm.alg;

import com.google.j2objc.annotations.AutoreleasePool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import im.actor.core.entity.Contact;
import im.actor.runtime.generic.mvvm.ChangeDescription;
import im.actor.runtime.storage.ListEngineItem;

// Disabling Bounds checks for speeding up calculations

/*-[
#define J2OBJC_DISABLE_ARRAY_BOUND_CHECKS 1
]-*/

public class Modifications {

    public static <T extends ListEngineItem> Modification<T> noOp() {
        return new Modification<T>() {
            @Override
            public List<ChangeDescription<T>> modify(ArrayList<T> sourceList) {
                return new ArrayList<>();
            }
        };
    }

    public static <T extends ListEngineItem> Modification<T> addOrUpdate(final T item) {
        ArrayList<T> res = new ArrayList<T>();
        res.add(item);
        return addOrUpdate(res);
    }

    public static <T extends ListEngineItem> Modification<T> addOrUpdate(final List<T> items) {
        return new Modification<T>() {
            @Override
            public List<ChangeDescription<T>> modify(ArrayList<T> sourceList) {
                ArrayList<ChangeDescription<T>> res = new ArrayList<>();
                addOrUpdate(items, sourceList, res, false);
                return res;
            }
        };
    }

    public static <T extends ListEngineItem> Modification<T> addLoadMore(final List<T> items) {
        return new Modification<T>() {
            @Override
            public List<ChangeDescription<T>> modify(ArrayList<T> sourceList) {
                ArrayList<ChangeDescription<T>> res = new ArrayList<>();
                addOrUpdate(items, sourceList, res, true);
                return res;
            }
        };
    }

    public static <T extends ListEngineItem> Modification<T> replace(final List<T> items) {
        return new Modification<T>() {
            @Override
            public List<ChangeDescription<T>> modify(ArrayList<T> sourceList) {
                ArrayList<ChangeDescription<T>> res = new ArrayList<>();
                replace(items, sourceList, res);
                return res;
            }
        };
    }

    public static <T extends ListEngineItem> Modification<T> remove(final long dstId) {
        return remove(new long[]{dstId});
    }

    public static <T extends ListEngineItem> Modification<T> remove(final long[] dstIds) {
        return new Modification<T>() {
            @Override
            public List<ChangeDescription<T>> modify(ArrayList<T> sourceList) {
                ArrayList<ChangeDescription<T>> res = new ArrayList<>();
                for (int i = 0; i < sourceList.size(); i++) {
                    ListEngineItem src = sourceList.get(i);
                    for (long aDstId : dstIds) {
                        if (src.getEngineId() == aDstId) {
                            sourceList.remove(i);
                            res.add(ChangeDescription.<T>remove(i));
                            i--;
                            break;
                        }
                    }
                }
                return res;
            }
        };
    }

    public static <T> Modification<T> clear() {
        return new Modification<T>() {
            @Override
            public List<ChangeDescription<T>> modify(ArrayList<T> sourceList) {
                ArrayList<ChangeDescription<T>> res = new ArrayList<>();
                if (sourceList.size() != 0) {
                    res.add(ChangeDescription.<T>remove(0, sourceList.size()));
                    sourceList.clear();
                }
                return res;
            }
        };
    }

    @AutoreleasePool
    private static <T extends ListEngineItem> void replace(List<T> items,
                                                           ArrayList<T> sourceList,
                                                           ArrayList<ChangeDescription<T>> changes) {
        // Remove missing items
        HashMap<Long, T> sourcePar = new HashMap<>();
        if (items != null && items.size() > 0 && items.get(0) instanceof Contact) {
            sourceList.clear();
        }else {
            outer:
            for (int i = 0; i < sourceList.size(); i++) {
                long id = sourceList.get(i).getEngineId();

                for (T itm : items) {
                    if (itm.getEngineId() == id) {
                        continue outer;
                    }
                }

                changes.add(ChangeDescription.<T>remove(i));
                sourceList.remove(i);
                i--;
            }

            if (sourceList.size() > 0) {
                Set<T> listSet = new HashSet<>(sourceList);
                for (T t : listSet) {
                    sourcePar.put(t.getEngineId(), t);
                }
            }
        }

        for (T itm : items) {
            addOrUpdate(itm, sourceList, sourcePar, changes, false);
        }
        if (sourceList != null && sourceList.size() > 0
                && sourceList.get(0) instanceof Contact) {
            Collections.sort(sourceList, (vo1, vo2) -> {
                String l, r = null;
                try {
                    Contact lhs = (Contact) vo1;
                    Contact rhs = (Contact) vo2;
                    l = lhs.getPyShort();
                    r = rhs.getPyShort();
                    if (r.equals("#")) {
                        return -1;
                    } else if (l.equals("#")) {
                        return 1;
                    }
                    int result = 0;
                    int i = 0;
                    if (l.charAt(i) < r.charAt(i)) {
                        result = -1;
                    } else if (l.charAt(i) > r.charAt(i)) {
                        result = 1;
                    } else {
                        result = 0;
                    }

                    if (result == 0) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                    return result;
                } catch (Exception e) {
                    System.out.println("iGem:" + e.toString());
                    e.printStackTrace();
                }
                return 0;
            });
        }
    }

    @AutoreleasePool
    private static <T extends ListEngineItem> void addOrUpdate(List<T> items,
                                                               ArrayList<T> sourceList,
                                                               ArrayList<ChangeDescription<T>> changes,
                                                               boolean isLoadMore) {
        for (T toAdd : items) {
            addOrUpdate(toAdd, sourceList, changes, isLoadMore);
        }
    }

    @AutoreleasePool
    private static <T extends ListEngineItem> void addOrUpdate(T item,
                                                               ArrayList<T> sourceList,
                                                               HashMap<Long, T> sourcePar,
                                                               ArrayList<ChangeDescription<T>> changes,
                                                               boolean isLoadMore) {
        long id = item.getEngineId();
        long sortKey = item.getEngineSort();
        // Finding suitable place for item
        int removedIndex = -1;
        int addedIndex = -1;
        boolean isHave = false;
        try {
            if (sourcePar.get(id) != null) {
                isHave = true;
            } else {
                isHave = false;
            }
        } catch (Exception e) {
            isHave = false;
        }
//        Optional<T> isHave = sourceList.stream().filter(srcItem -> srcItem.getEngineId() == id).findFirst();
        if (isHave) {
            for (int i = 0; i < sourceList.size(); i++) {
                T srcItem = sourceList.get(i);
                if (srcItem.getEngineId() == id) {
                    if (isLoadMore) {
                        return;
                    }
                    // Remove old item
                    sourceList.remove(i);
                    if (addedIndex >= 0) {
                        removedIndex = i - 1;
                    } else {
                        removedIndex = i;
                    }
                    i--;
                    continue;
                } else {
                    // TODO: Fix ADD ONLY
                    if ((addedIndex < 0) && sortKey > srcItem.getEngineSort()) {
                        addedIndex = i;
                        sourceList.add(i, item);
                        sourcePar.put(id, item);
                        i++;
                    }
                }

                // Already founded
                if (addedIndex >= 0 && removedIndex >= 0) {
                    break;
                }
            }
        }


        // If no place for insert: insert to end
        if (addedIndex < 0) {
            addedIndex = sourceList.size();
            sourceList.add(sourceList.size(), item);
            sourcePar.put(id, item);
        }

        if (addedIndex == removedIndex) {
            // If there are no movement: just update item in place
            changes.add(ChangeDescription.update(addedIndex, item));
        } else if (removedIndex >= 0) {
            // Movement + update occurred
            changes.add(ChangeDescription.update(removedIndex, item));
            changes.add(ChangeDescription.<T>move(removedIndex, addedIndex));
        } else {
            // No old element found: add new element
            changes.add(ChangeDescription.add(addedIndex, item));
        }
    }

    @AutoreleasePool
    private static <T extends ListEngineItem> void addOrUpdate(T item,
                                                               ArrayList<T> sourceList,
                                                               ArrayList<ChangeDescription<T>> changes,
                                                               boolean isLoadMore) {
        long id = item.getEngineId();
        long sortKey = item.getEngineSort();

        // Finding suitable place for item
        int removedIndex = -1;
        int addedIndex = -1;

        for (int i = 0; i < sourceList.size(); i++) {
            T srcItem = sourceList.get(i);
            if (srcItem.getEngineId() == id) {
                if (isLoadMore) {
                    return;
                }
                // Remove old item
                sourceList.remove(i);
                if (addedIndex >= 0) {
                    removedIndex = i - 1;
                } else {
                    removedIndex = i;
                }
                i--;
                continue;
            } else {
                // TODO: Fix ADD ONLY
                if ((addedIndex < 0) && sortKey > srcItem.getEngineSort()) {
                    addedIndex = i;
                    sourceList.add(i, item);
                    i++;
                }
            }

            // Already founded
            if (addedIndex >= 0 && removedIndex >= 0) {
                break;
            }
        }


        // If no place for insert: insert to end
        if (addedIndex < 0) {
            addedIndex = sourceList.size();
            sourceList.add(sourceList.size(), item);
        }

        if (addedIndex == removedIndex) {
            // If there are no movement: just update item in place
            changes.add(ChangeDescription.update(addedIndex, item));
        } else if (removedIndex >= 0) {
            // Movement + update occurred
            changes.add(ChangeDescription.update(removedIndex, item));
            changes.add(ChangeDescription.<T>move(removedIndex, addedIndex));
        } else {
            // No old element found: add new element
            changes.add(ChangeDescription.add(addedIndex, item));
        }
    }

}
