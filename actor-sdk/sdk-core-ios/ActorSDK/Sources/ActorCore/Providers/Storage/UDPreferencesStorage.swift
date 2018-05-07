//
//  Copyright (c) 2014-2016 Actor LLC. <https://actor.im>
//

import Foundation

@objc class UDPreferencesStorage: NSObject, ARPreferencesStorage {
    
    let prefs = UserDefaults.standard
    
    var cachedPrefs = [String: AnyObject?]()
    
    func putLong(withKey key: String!, withValue v: jlong) {
        setObject(key, obj: NSNumber(value: v as Int64))
    }

    func getLongWithKey(_ key: String!, withDefault def: jlong) -> jlong {
//        print("一等奖1===========")
        let val = fetchObj(key)
//        print("一等奖2===========")
        if (val == nil || !(val is NSNumber)) {
//            print("一等奖3===========")
            return def;
        } else {
//            print("一等奖4===========")
            return (val as! NSNumber).int64Value
        }
    }
    
    func putInt(withKey key: String!, withValue v: jint) {
        setObject(key, obj: Int(v) as AnyObject?)
    }
    
    func getIntWithKey(_ key: String!, withDefault def: jint) -> jint {
        
        let val: AnyObject? = fetchObj(key)
        if (val == nil || !(val is NSNumber)) {
//            print("二等奖1===========")
            return def;
        } else {
//            print("二等奖2===========")
            return (val as! NSNumber).int32Value
        }
    }
    
    func putBool(withKey key: String!, withValue v: Bool) {
        setObject(key, obj: v as AnyObject?)
    }
    
    func getBoolWithKey(_ key: String!, withDefault def: Bool) -> Bool {
        let val: AnyObject? = fetchObj(key);
        print("二等奖8888===========")
        if (val == nil || (!(val is Bool))) {
            return def
        } else {
            return val as! Bool;
        }
    }
    
    func putBytes(withKey key: String!, withValue v: IOSByteArray!) {
        if (v == nil) {
            setObject(key, obj: nil)
        } else {
            setObject(key, obj: v.toNSData() as AnyObject?)
        }
    }
    
    func getBytesWithKey(_ key: String!) -> IOSByteArray! {
        let val = fetchObj(key);
        if (val == nil || !(val is NSData)){
            return nil
        } else {
            return (val as! Data).toJavaBytes()
        }
    }
    
    func putString(withKey key: String!, withValue v: String!) {
        setObject(key, obj: v as AnyObject?)
    }
    
    func getStringWithKey(_ key: String!) -> String! {
        let val = fetchObj(key);
        if (val == nil || !(val is String)) {
            return nil
        } else {
            return val as! String
        }
    }
    
    func clear() {
        let appDomain = Bundle.main.bundleIdentifier!
        prefs.removePersistentDomain(forName: appDomain)
    }
    
    
    //
    // Interface
    //
    
    fileprivate func setObject(_ key: String, obj: AnyObject?) {
        if obj != nil {
            prefs.set(obj, forKey: key)
            cachedPrefs[key] = obj
        } else {
            prefs.removeObject(forKey: key)
            cachedPrefs.removeValue(forKey: key)
        }
        prefs.synchronize()
    }
    
    fileprivate func fetchObj(_ key: String) -> AnyObject? {
        print("key===="+key)
        if let obj = cachedPrefs[key] {
            print("一等奖5===========")
           return obj
        }
//        let res = prefs.object(forKey: key)
        let res:AnyObject?
        if (prefs.object(forKey: key) != nil) {
            res = prefs.object(forKey: key) as AnyObject
            log("一等奖6===========")
        }
        else {
            res = nil
            log("一等奖7===========")
        }
        cachedPrefs[key] = res as AnyObject??
        return res as AnyObject?
    }
}
