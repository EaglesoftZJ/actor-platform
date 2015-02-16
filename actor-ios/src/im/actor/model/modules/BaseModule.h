//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/ex3ndr/Develop/actor-model/actor-ios/build/java/im/actor/model/modules/BaseModule.java
//

#ifndef _ImActorModelModulesBaseModule_H_
#define _ImActorModelModulesBaseModule_H_

@class AMRpcException;
@class ImActorModelModulesModules;
@class ImActorModelModulesUpdates;
@class ImActorModelNetworkParserRequest;
@class ImActorModelNetworkParserResponse;
@protocol ImActorModelMvvmKeyValueEngine;
@protocol ImActorModelStoragePreferencesStorage;
@protocol JavaLangRunnable;

#include "J2ObjC_header.h"
#include "im/actor/model/network/RpcCallback.h"

@interface ImActorModelModulesBaseModule : NSObject {
}

- (instancetype)initWithImActorModelModulesModules:(ImActorModelModulesModules *)modules;

- (ImActorModelModulesModules *)modules;

- (ImActorModelModulesUpdates *)updates;

- (void)runOnUiThreadWithJavaLangRunnable:(id<JavaLangRunnable>)runnable;

- (id<ImActorModelStoragePreferencesStorage>)preferences;

- (jint)myUid;

- (void)requestWithImActorModelNetworkParserRequest:(ImActorModelNetworkParserRequest *)request
                                  withAMRpcCallback:(id<AMRpcCallback>)callback;

- (void)requestWithImActorModelNetworkParserRequest:(ImActorModelNetworkParserRequest *)request;

- (id<ImActorModelMvvmKeyValueEngine>)users;

@end

J2OBJC_EMPTY_STATIC_INIT(ImActorModelModulesBaseModule)

CF_EXTERN_C_BEGIN
CF_EXTERN_C_END

J2OBJC_TYPE_LITERAL_HEADER(ImActorModelModulesBaseModule)

@interface ImActorModelModulesBaseModule_$1 : NSObject < AMRpcCallback > {
}

- (void)onResultWithImActorModelNetworkParserResponse:(ImActorModelNetworkParserResponse *)response;

- (void)onErrorWithAMRpcException:(AMRpcException *)e;

- (instancetype)init;

@end

J2OBJC_EMPTY_STATIC_INIT(ImActorModelModulesBaseModule_$1)

CF_EXTERN_C_BEGIN
CF_EXTERN_C_END

J2OBJC_TYPE_LITERAL_HEADER(ImActorModelModulesBaseModule_$1)

#endif // _ImActorModelModulesBaseModule_H_
