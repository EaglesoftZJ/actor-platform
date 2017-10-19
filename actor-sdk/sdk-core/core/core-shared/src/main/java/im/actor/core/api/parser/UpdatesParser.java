package im.actor.core.api.parser;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import im.actor.runtime.bser.*;
import im.actor.runtime.collections.*;
import static im.actor.runtime.bser.Utils.*;
import im.actor.core.network.parser.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import com.google.j2objc.annotations.ObjectiveCName;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import im.actor.core.api.updates.*;

public class UpdatesParser extends BaseParser<Update> {
    @Override
    public Update read(int type, byte[] payload) throws IOException {
//        UpdateUserExtChanged.fromBytes(payload);
//        UpdateUserAboutChanged.fromBytes(payload);
        switch(type) {
            case 16: return UpdateUserAvatarChanged.fromBytes(payload);
            case 32: return UpdateUserNameChanged.fromBytes(payload);
            case 51: return UpdateUserLocalNameChanged.fromBytes(payload);
            case 134: return UpdateUserContactsChanged.fromBytes(payload);
            case 209: return UpdateUserNickChanged.fromBytes(payload);
            case 210: return UpdateUserAboutChanged.fromBytes(payload);
            case 212: return UpdateUserPreferredLanguagesChanged.fromBytes(payload);
            case 216: return UpdateUserTimeZoneChanged.fromBytes(payload);
            case 217: return UpdateUserBotCommandsChanged.fromBytes(payload);
            case 218: return UpdateUserExtChanged.fromBytes(payload);
            case 219: return UpdateUserFullExtChanged.fromBytes(payload);
            case 5: return UpdateContactRegistered.fromBytes(payload);
            case 40: return UpdateContactsAdded.fromBytes(payload);
            case 41: return UpdateContactsRemoved.fromBytes(payload);
            case 2629: return UpdateUserBlocked.fromBytes(payload);
            case 2630: return UpdateUserUnblocked.fromBytes(payload);
            case 55: return UpdateMessage.fromBytes(payload);
            case 162: return UpdateMessageContentChanged.fromBytes(payload);
            case 169: return UpdateMessageQuotedChanged.fromBytes(payload);
            case 163: return UpdateMessageDateChanged.fromBytes(payload);
            case 4: return UpdateMessageSent.fromBytes(payload);
            case 54: return UpdateMessageReceived.fromBytes(payload);
            case 19: return UpdateMessageRead.fromBytes(payload);
            case 50: return UpdateMessageReadByMe.fromBytes(payload);
            case 46: return UpdateMessageDelete.fromBytes(payload);
            case 47: return UpdateChatClear.fromBytes(payload);
            case 48: return UpdateChatDelete.fromBytes(payload);
            case 94: return UpdateChatArchive.fromBytes(payload);
            case 2690: return UpdateChatDropCache.fromBytes(payload);
            case 1: return UpdateChatGroupsChanged.fromBytes(payload);
            case 222: return UpdateReactionsUpdate.fromBytes(payload);
            case 2609: return UpdateGroupTitleChanged.fromBytes(payload);
            case 2610: return UpdateGroupAvatarChanged.fromBytes(payload);
            case 2616: return UpdateGroupTopicChanged.fromBytes(payload);
            case 2617: return UpdateGroupAboutChanged.fromBytes(payload);
            case 2613: return UpdateGroupExtChanged.fromBytes(payload);
            case 2618: return UpdateGroupFullExtChanged.fromBytes(payload);
            case 2628: return UpdateGroupShortNameChanged.fromBytes(payload);
            case 2619: return UpdateGroupOwnerChanged.fromBytes(payload);
            case 2620: return UpdateGroupHistoryShared.fromBytes(payload);
            case 2658: return UpdateGroupDeleted.fromBytes(payload);
            case 2663: return UpdateGroupPermissionsChanged.fromBytes(payload);
            case 2664: return UpdateGroupFullPermissionsChanged.fromBytes(payload);
            case 2612: return UpdateGroupMemberChanged.fromBytes(payload);
            case 2615: return UpdateGroupMembersBecameAsync.fromBytes(payload);
            case 2614: return UpdateGroupMembersUpdated.fromBytes(payload);
            case 2623: return UpdateGroupMemberDiff.fromBytes(payload);
            case 2622: return UpdateGroupMembersCountChanged.fromBytes(payload);
            case 2627: return UpdateGroupMemberAdminChanged.fromBytes(payload);
            case 36: return UpdateGroupInviteObsolete.fromBytes(payload);
            case 21: return UpdateGroupUserInvitedObsolete.fromBytes(payload);
            case 23: return UpdateGroupUserLeaveObsolete.fromBytes(payload);
            case 24: return UpdateGroupUserKickObsolete.fromBytes(payload);
            case 44: return UpdateGroupMembersUpdateObsolete.fromBytes(payload);
            case 38: return UpdateGroupTitleChangedObsolete.fromBytes(payload);
            case 213: return UpdateGroupTopicChangedObsolete.fromBytes(payload);
            case 214: return UpdateGroupAboutChangedObsolete.fromBytes(payload);
            case 39: return UpdateGroupAvatarChangedObsolete.fromBytes(payload);
            case 161: return UpdateOwnStickersChanged.fromBytes(payload);
            case 164: return UpdateStickerCollectionsChanged.fromBytes(payload);
            case 165: return UpdateOwnTeamsChanged.fromBytes(payload);
            case 166: return UpdatePauseNotifications.fromBytes(payload);
            case 167: return UpdateRestoreNotifications.fromBytes(payload);
            case 6: return UpdateTyping.fromBytes(payload);
            case 81: return UpdateTypingStop.fromBytes(payload);
            case 7: return UpdateUserOnline.fromBytes(payload);
            case 8: return UpdateUserOffline.fromBytes(payload);
            case 9: return UpdateUserLastSeen.fromBytes(payload);
            case 33: return UpdateGroupOnline.fromBytes(payload);
            case 2561: return UpdateEventBusDeviceConnected.fromBytes(payload);
            case 2563: return UpdateEventBusDeviceDisconnected.fromBytes(payload);
            case 2562: return UpdateEventBusMessage.fromBytes(payload);
            case 2564: return UpdateEventBusDisposed.fromBytes(payload);
            case 72: return UpdateSynedSetUpdated.fromBytes(payload);
            case 73: return UpdateSyncedSetAddedOrUpdated.fromBytes(payload);
            case 74: return UpdateSyncedSetRemoved.fromBytes(payload);
            case 52: return UpdateIncomingCall.fromBytes(payload);
            case 53: return UpdateCallHandled.fromBytes(payload);
            case 56: return UpdateCallUpgraded.fromBytes(payload);
            case 131: return UpdateParameterChanged.fromBytes(payload);
            case 103: return UpdatePublicKeyGroupChanged.fromBytes(payload);
            case 112: return UpdateKeysAdded.fromBytes(payload);
            case 113: return UpdateKeysRemoved.fromBytes(payload);
            case 104: return UpdatePublicKeyGroupAdded.fromBytes(payload);
            case 105: return UpdatePublicKeyGroupRemoved.fromBytes(payload);
            case 177: return UpdateEncryptedPackage.fromBytes(payload);
            case 80: return UpdateRawUpdate.fromBytes(payload);
            case 85: return UpdateEmptyUpdate.fromBytes(payload);
            case 215: return UpdateCountersChanged.fromBytes(payload);
            case 42: return UpdateConfig.fromBytes(payload);
        }
        throw new IOException();
    }
}
