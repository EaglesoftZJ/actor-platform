package im.actor.runtime.generic;


import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.runtime.generic.mvvm.DisplayList;
import im.actor.runtime.generic.storage.AsyncListEngine;
import im.actor.runtime.EnginesRuntime;
import im.actor.runtime.bser.BserCreator;
import im.actor.runtime.bser.BserObject;
import im.actor.runtime.mvvm.PlatformDisplayList;
import im.actor.runtime.storage.ListEngine;
import im.actor.runtime.storage.ListEngineItem;
import im.actor.runtime.storage.ListStorage;
import im.actor.runtime.storage.ListStorageDisplayEx;

public class GenericEnginesProvider implements EnginesRuntime {

//    static {
//        ActorSystem.system().addDispatcher("display_list");
//        ActorSystem.system().addDispatcher("db", 1);
//    }

    private DisplayList.OperationMode operationMode;

    public GenericEnginesProvider(DisplayList.OperationMode operationMode) {
        this.operationMode = operationMode;
    }

    @Override
    public <T extends BserObject & ListEngineItem> ListEngine<T> createListEngine(ListStorage storage, BserCreator<T> creator) {
        return new AsyncListEngine<T>((ListStorageDisplayEx) storage, creator);
    }

    @Override
    public <T extends BserObject & ListEngineItem> PlatformDisplayList<T> createDisplayList(ListEngine<T> listEngine, boolean isSharedInstance, String clazz) {
        int pageSize = 20;
        int loadGap = 20;
        if (listEngine.getHeadValue() != null) {
            String className = listEngine.getHeadValue().getClass().getName().trim();
            if ("im.actor.core.entity.Contact".equals(className)||"ACContact".equals(className)) {
                pageSize = 10000;
            }
        }
        BindedDisplayList<T> list = new BindedDisplayList<T>((AsyncListEngine<T>) listEngine, isSharedInstance, pageSize, loadGap, operationMode);
        return list;
    }
}
