//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/ex3ndr/Develop/actor-model/actor-ios/build/java/im/actor/model/modules/updates/TypingProcessor.java
//

#include "J2ObjC_source.h"
#include "im/actor/model/api/Peer.h"
#include "im/actor/model/api/PeerType.h"
#include "im/actor/model/droidkit/actors/ActorRef.h"
#include "im/actor/model/modules/Modules.h"
#include "im/actor/model/modules/typing/TypingActor.h"
#include "im/actor/model/modules/updates/TypingProcessor.h"

@interface ImActorModelModulesUpdatesTypingProcessor () {
 @public
  ImActorModelDroidkitActorsActorRef *typingActor_;
}
@end

J2OBJC_FIELD_SETTER(ImActorModelModulesUpdatesTypingProcessor, typingActor_, ImActorModelDroidkitActorsActorRef *)

@implementation ImActorModelModulesUpdatesTypingProcessor

- (instancetype)initWithImActorModelModulesModules:(ImActorModelModulesModules *)modules {
  if (self = [super initWithImActorModelModulesModules:modules]) {
    self->typingActor_ = ImActorModelModulesTypingTypingActor_getWithImActorModelModulesModules_(modules);
  }
  return self;
}

- (void)onTypingWithImActorModelApiPeer:(ImActorModelApiPeer *)peer
                                withInt:(jint)uid
                                withInt:(jint)type {
  if ([((ImActorModelApiPeer *) nil_chk(peer)) getType] == ImActorModelApiPeerTypeEnum_get_PRIVATE()) {
    [((ImActorModelDroidkitActorsActorRef *) nil_chk(typingActor_)) sendOnceWithId:[[ImActorModelModulesTypingTypingActor_PrivateTyping alloc] initWithInt:uid withInt:type]];
  }
  else if ([peer getType] == ImActorModelApiPeerTypeEnum_get_GROUP()) {
    [((ImActorModelDroidkitActorsActorRef *) nil_chk(typingActor_)) sendOnceWithId:[[ImActorModelModulesTypingTypingActor_GroupTyping alloc] initWithInt:[peer getId] withInt:uid withInt:type]];
  }
  else {
  }
}

- (void)onMessageWithImActorModelApiPeer:(ImActorModelApiPeer *)peer
                                 withInt:(jint)uid {
  if ([((ImActorModelApiPeer *) nil_chk(peer)) getType] == ImActorModelApiPeerTypeEnum_get_PRIVATE()) {
    [((ImActorModelDroidkitActorsActorRef *) nil_chk(typingActor_)) sendOnceWithId:[[ImActorModelModulesTypingTypingActor_StopTyping alloc] initWithInt:uid]];
  }
  else if ([peer getType] == ImActorModelApiPeerTypeEnum_get_GROUP()) {
    [((ImActorModelDroidkitActorsActorRef *) nil_chk(typingActor_)) sendOnceWithId:[[ImActorModelModulesTypingTypingActor_StopGroupTyping alloc] initWithInt:[peer getId] withInt:uid]];
  }
  else {
  }
}

- (void)copyAllFieldsTo:(ImActorModelModulesUpdatesTypingProcessor *)other {
  [super copyAllFieldsTo:other];
  other->typingActor_ = typingActor_;
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "initWithImActorModelModulesModules:", "TypingProcessor", NULL, 0x1, NULL },
    { "onTypingWithImActorModelApiPeer:withInt:withInt:", "onTyping", "V", 0x1, NULL },
    { "onMessageWithImActorModelApiPeer:withInt:", "onMessage", "V", 0x1, NULL },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "typingActor_", NULL, 0x2, "Lim.actor.model.droidkit.actors.ActorRef;", NULL,  },
  };
  static const J2ObjcClassInfo _ImActorModelModulesUpdatesTypingProcessor = { 1, "TypingProcessor", "im.actor.model.modules.updates", NULL, 0x1, 3, methods, 1, fields, 0, NULL};
  return &_ImActorModelModulesUpdatesTypingProcessor;
}

@end

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ImActorModelModulesUpdatesTypingProcessor)
