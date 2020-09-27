package im.actor;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.xiaomi.mipush.sdk.MiPushClient;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.multidex.MultiDex;
import im.actor.core.entity.Message;
import im.actor.core.entity.Peer;
import im.actor.core.entity.content.JsonContent;
import im.actor.core.entity.content.PhotoContent;
import im.actor.develop.R;
import im.actor.fragments.AttachFragmentEx;
import im.actor.fragments.RootFragmentEx;
import im.actor.runtime.json.JSONException;
import im.actor.runtime.json.JSONObject;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.ActorSDKApplication;
import im.actor.sdk.ActorStyle;
import im.actor.sdk.BaseActorSDKDelegate;
import im.actor.sdk.controllers.conversation.attach.AbsAttachFragment;
import im.actor.sdk.controllers.conversation.messages.BubbleLayouter;
import im.actor.sdk.controllers.conversation.messages.DefaultLayouter;
import im.actor.sdk.controllers.conversation.messages.JsonXmlBubbleLayouter;
import im.actor.sdk.controllers.conversation.messages.XmlBubbleLayouter;
import im.actor.sdk.controllers.conversation.messages.content.PhotoHolder;
import im.actor.sdk.controllers.conversation.messages.content.TextHolder;
import im.actor.sdk.controllers.conversation.messages.content.preprocessor.PreprocessedData;
import im.actor.sdk.controllers.settings.ActorSettingsCategories;
import im.actor.sdk.controllers.settings.ActorSettingsCategory;
import im.actor.sdk.controllers.settings.ActorSettingsField;
import im.actor.sdk.controllers.settings.BaseActorSettingsActivity;
import im.actor.sdk.controllers.settings.BaseActorSettingsFragment;
import im.actor.sdk.intents.ActorIntentFragmentActivity;
import im.actor.sdk.push.Utils;

public class Application extends ActorSDKApplication {

    // user your appid the key.
    private static final String APP_ID = "2882303761517562000";
    // user your appid the key.
    private static final String APP_KEY = "5731756231000";

    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
        int phoneFlag = Utils.isWhatPhone();
//        if(phoneFlag == 1){
//            HMSAgent.init(this);
//        }

        if (phoneFlag == 0) {
//百度推送
            PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
                    Utils.getMetaValue(this, "api_key"));
        } else if (phoneFlag == 1) {
// 华为推送
//            HMSAgent.init(this);
//            HuaweiIdSignInOptions options = new HuaweiIdSignInOptions.Builder(HuaweiIdSignInOptions.DEFAULT_SIGN_IN)
//                    .build();
//            huaWeiCallBack callBack = new huaWeiCallBack(this);
//            client = new HuaweiApiClient.Builder(this) //
//                    .addApi(HuaweiId.SIGN_IN_API, options)//
//                    .addConnectionCallbacks(callBack) //
//                    .addOnConnectionFailedListener(callBack) //
//                    .build();
//            client.connect();
        } else if (phoneFlag == 2) {
            //小米推送
//            注意：因为推送服务XMPushService在AndroidManifest.xml中设置为运行在另外一个进程，这导致本Application会被实例化两次，所以我们需要让应用的主进程初始化。
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        int phoneFlag = Utils.isWhatPhone();
        if (phoneFlag == 1) {
//            HMSAgent.destroy();
        }

    }

    @Override
    public void onConfigureActorSDK() {
        ActorSDK.sharedActor().setDelegate(new ActorSDKDelegate());
        ActorSDK.sharedActor().setPushId(209133700967L);
        ActorSDK.sharedActor().setOnClientPrivacyEnabled(true);

        ActorStyle style = ActorSDK.sharedActor().style;
        style.setDialogsActiveTextColor(0xff5882ac);
        style.setShowAvatarPrivateInTitle(false);

        ActorSDK.sharedActor().setFastShareEnabled(true);

        ActorSDK.sharedActor().setCallsEnabled(true);

        ActorSDK.sharedActor().setTosUrl("http://actor.im");
        ActorSDK.sharedActor().setPrivacyText("bla bla bla");

        ActorSDK.sharedActor().setVideoCallsEnabled(true);

        ActorSDK.sharedActor().setAutoJoinGroups(new String[]{
                "actor_news"
        });


//        ActorSDK.sharedActor().setTwitter("");
//        ActorSDK.sharedActor().setHomePage("http://www.foo.com");
//        ActorSDK.sharedActor().setInviteUrl("http://www.foo.com");
//        ActorSDK.sharedActor().setCallsEnabled(true);

//        ActorSDK.sharedActor().setEndpoints(new String[]{"tcp://192.168.1.184:9070"});

//        ActorStyle style = ActorSDK.sharedActor().style;
//        style.setMainColor(Color.parseColor("#529a88"));
//        style.setAvatarBackgroundResource(R.drawable.img_profile_avatar_default);
//        AbsContent.registerConverter(new ContentConverter() {
//            @Override
//            public AbsContent convert(AbsContentContainer container) {
//                return JsonContent.convert(container, new TCBotMesaage());
//            }
//
//            @Override
//            public boolean validate(AbsContent content) {
//                return content instanceof TCBotMesaage;
//            }
//        });
    }

    private class ActorSDKDelegate extends BaseActorSDKDelegate {

        @Override
        public void configureChatViewHolders(ArrayList<BubbleLayouter> layouters) {
//            layouters.add(0, new BubbleTextHolderLayouter());
            layouters.add(0, new DefaultLayouter(DefaultLayouter.TEXT_HOLDER, (adapter2, root2, peer2) -> new TextHolder(adapter2, root2, peer2) {
                @Override
                public void bindRawText(CharSequence rawText, long readDate, long receiveDate, Spannable reactions, Message message, boolean isItalic) {
                    super.bindRawText(rawText, readDate, receiveDate, reactions, message, isItalic);
                    text.append("\n\n" + message.getSortDate());
                }
            }));

            layouters.add(0, new DefaultLayouter(DefaultLayouter.TEXT_HOLDER, CensoredTextHolderEx::new));

//            PhotoHolder photoHolder = new PhotoHolder(adapter1, root1, peer1)
            layouters.add(0, new XmlBubbleLayouter(content -> content instanceof PhotoContent, R.layout.adapter_dialog_photo, (adapter1, root1, peer1) -> new PhotoHolder(adapter1, root1, peer1) {
                @Override
                protected void onConfigureViewHolder() {
//                    previewView.setColorFilter(ActorStyle.adjustColorAlpha(Color.CYAN, 20), PorterDuff.Mode.ADD);
                }
            }));
            layouters.add(0, new JsonXmlBubbleLayouter(null, R.layout.adapter_dialog_text, (adapter, root, peer) -> new TextHolder(adapter, root, peer) {
                @Override
                protected void bindData(Message message, long readDate, long receiveDate, boolean isUpdated, PreprocessedData preprocessedData) {
                    String jsonString = "can't read json";
                    long delId = 0;
                    try {
                        JSONObject jsonObject = new JSONObject(((JsonContent) message.getContent()).getRawJson());
                        String dataType = jsonObject.getString("dataType");
                        JSONObject data = jsonObject.getJSONObject("data");

                        if (dataType.equals("revert")) {
                            try {
                                delId = Long.parseLong(data.getString("rid"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        jsonString = data.getString("text");
                        if (delId != 0) {
                            if (message.getSenderId() == ActorSDK.sharedActor().getMessenger().myUid()) {
                                jsonString = "你撤回了一条消息";
                            }
                            ActorSDK.sharedActor().getMessenger().deleteMessages(peer, new long[]{delId});
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    bindRawText(jsonString, readDate, receiveDate, reactions, message, false);
                }
            }));
        }

        @Nullable
        @Override
        public Fragment fragmentForRoot() {
            return new RootFragmentEx();
        }

        @Nullable
        @Override
        public AbsAttachFragment fragmentForAttachMenu(Peer peer) {
            return new AttachFragmentEx(peer);
        }

//        @Override
//        public BaseGroupInfoActivity getGroupInfoIntent(int gid) {
//            return new BaseGroupInfoActivity() {
//                @Override
//                public GroupInfoFragment getGroupInfoFragment(int chatId) {
//                    return GroupInfoEx.create(chatId);
//                }
//            };
//        }

        @Override
        public ActorIntentFragmentActivity getSettingsIntent() {
            return new BaseActorSettingsActivity() {
                @Override
                public BaseActorSettingsFragment getSettingsFragment() {
                    return new BaseActorSettingsFragment() {
                        CheckBox blablaCheckBox;

                        @Override
                        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                            blablaCheckBox = new CheckBox(getContext());
                            return super.onCreateView(inflater, container, savedInstanceState);
                        }

                        @Override
                        public ActorSettingsCategories getBeforeSettingsCategories() {
                            return new ActorSettingsCategories()
                                    .addCategory(new ActorSettingsCategory("azaza")
                                            .addField(new ActorSettingsField(R.id.terminateSessions)
                                                    .setName("blabla")
                                                    .setIconResourceId(R.drawable.ic_edit_black_24dp)
                                                    .setRightView(blablaCheckBox)
                                            )
                                    );
                        }

                        @Override
                        public View.OnClickListener getMenuFieldOnClickListener() {
                            return new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    switch (v.getId()) {
                                        case R.id.terminateSessions:
                                            Toast.makeText(v.getContext(), "hey", Toast.LENGTH_LONG).show();
                                            blablaCheckBox.toggle();
                                            break;
                                    }
                                }
                            };
                        }

                    };
                }
            };
        }
    }

}
