package im.actor.sdk.intents;

import java.io.Serializable;
import java.util.Map;

/**
 * 序列化map供Bundle传递map使用
 */
public class SerializableMap implements Serializable {

    private Map<String, Object> map;

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public Map<String, Object> addMap(SerializableMap smap2) {
        Map<String, Object> map2 = smap2.getMap();
        this.map = addMapTo(this.map, map2);
        return this.map;
    }

    public  Map<String, Object> addMapTo(Map<String, Object> target, Map<String, Object> plus) {
        Object[] os = plus.keySet().toArray();
        String key;
        for (int i = 0; i < os.length; i++) {
            key = (String) os[i];
            if (!target.containsKey(key))
                target.put(key, plus.get(key));
        }
        return target;
    }
}
