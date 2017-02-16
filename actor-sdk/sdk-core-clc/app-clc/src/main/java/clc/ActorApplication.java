package clc;


import im.actor.core.*;
import im.actor.core.api.rpc.RequestEditAbout;
import im.actor.core.api.rpc.ResponseSeq;
import im.actor.core.api.updates.UpdateUserAboutChanged;
import im.actor.core.entity.*;
import im.actor.core.entity.content.TextContent;
import im.actor.core.network.RpcCallback;
import im.actor.core.network.RpcException;
import im.actor.core.providers.NotificationProvider;
import im.actor.core.providers.PhoneBookProvider;
import im.actor.core.viewmodel.Command;
import im.actor.core.viewmodel.CommandCallback;
import im.actor.core.viewmodel.UserVM;
import im.actor.runtime.clc.ClcJavaPreferenceStorage;
import im.actor.runtime.generic.mvvm.AndroidListUpdate;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.runtime.generic.mvvm.DisplayList;
import im.actor.sdk.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.prefs.BackingStoreException;


public class ActorApplication {

    private String url;
    private String username;
    private String password;
    int myNumber = 1;
    public ConfigurationBuilder builder;
    private static final Logger logger = LoggerFactory.getLogger(ClcApplication.class);

    private static int randomSeed;
    static ClcMessenger messenger;


    public ActorApplication(String url,String username,String password)
    {
        this.url = url;
        this.username = username;
        this.password = password;

        messenger = new ClcMessenger(builder.build(),Integer.toString(myNumber));
        builder = new ConfigurationBuilder();

        builder.addEndpoint(this.url);



        builder.setDeviceCategory(DeviceCategory.DESKTOP);
        builder.setPlatformType(PlatformType.GENERIC);
        builder.setApiConfiguration(new ApiConfiguration(
                "cli",
                1,
                "4295f9666fad3faf2d04277fe7a0c40ff39a85d313de5348ad8ffa650ad71855",
                "najva00000000000000000123-" + myNumber,
                "najva00000000000000000000-v1-" + myNumber));

        messenger = new ClcMessenger(builder.build(),Integer.toString(myNumber));



//        messenger.resetAuth();
        sendUserName(myNumber);
    }

    public void sendUserName(String username,String password)
    {
        messenger.requestStartUserNameAuth(username).start(new CommandCallback<AuthState>() {
            @Override
            public void onResult(AuthState res) {
                logger.info(res.toString());
                sendPassword(password);
            }

            @Override
            public void onError(Exception e) {
                logger.error(e.getMessage(),e);
            }
        });
    }

    public void sendPassword(String password) {
        try {
            messenger.validatePassword(password).start(new CommandCallback<AuthState>() {
                @Override
                public void onResult(AuthState res) {
                    randomSeed = new Random().nextInt();
                    if (res == AuthState.SIGN_UP) {
                        logger.info("SIGN_UP");
                        signUp(username, Sex.MALE,null, password);
                    } else if(res == AuthState.LOGGED_IN){
                        logger.info("LOGGED_IN");
                    }

                }

                @Override
                public void onError(Exception e) {

                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

    public void signUp(final String name, Sex sex, String avatarPath, String password) {
        messenger.signUp(name, sex, avatarPath,password).start(new CommandCallback<AuthState>() {
            @Override
            public void onResult(AuthState res) {
                if (res == AuthState.LOGGED_IN){
                    logger.info("LOGGED_IN");
//                    sendMessage("+989150000" + (myNumber + 20), "seed: " + randomSeed + "," + myNumber);
                }else if(res == AuthState.SIGN_UP){
                    logger.info("SIGN_UP");
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}