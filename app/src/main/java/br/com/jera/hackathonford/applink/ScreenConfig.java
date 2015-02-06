package br.com.jera.hackathonford.applink;

import android.content.Context;
import android.content.Intent;

import com.ford.syncV4.exception.SyncException;
import com.ford.syncV4.proxy.SyncProxyALM;
import com.ford.syncV4.proxy.rpc.OnButtonPress;
import com.ford.syncV4.proxy.rpc.Show;
import com.ford.syncV4.proxy.rpc.SoftButton;
import com.ford.syncV4.proxy.rpc.enums.SoftButtonType;
import com.ford.syncV4.proxy.rpc.enums.SystemAction;

import java.util.Vector;

import br.com.jera.hackathonford.receiver.PanicoReceiver;
import br.com.jera.hackathonford.utils.Constants;
import br.com.jera.hackathonford.utils.Logger;

/**
 * Created by marco on 06/02/15.
 */
public class ScreenConfig {

    public static int initialScreen(int autoIncCorrId, SyncProxyALM proxy){
        Vector<SoftButton> mainSoftButtons = new Vector<SoftButton>();
        Show msg = new Show();
        msg.setCorrelationID(autoIncCorrId++);
        msg.setMainField1("Seja bem vindo ao");
        msg.setMainField2("Heere");
        mainSoftButtons.add(sosButton());
        msg.setSoftButtons(mainSoftButtons);
        try {
            proxy.sendRPCRequest(msg);
        } catch (SyncException e) {
            Logger.i("sync exception" + e.getMessage()
                            + e.getSyncExceptionCause());
            e.printStackTrace();
        }
        return autoIncCorrId;
    }

    private static SoftButton accidentButton() {
        SoftButton button = new SoftButton();
        button.setSoftButtonID(Constants.SoftButtons.SOS_BUTTON);
        button.setText("Acidente");
        button.setType(SoftButtonType.SBT_TEXT);
        button.setIsHighlighted(false);
        button.setSystemAction(SystemAction.DEFAULT_ACTION);
        return button;
    }

    private static SoftButton sosButton() {
        SoftButton button = new SoftButton();
        button.setSoftButtonID(Constants.SoftButtons.SOS_BUTTON);
        button.setText("SOS");
        button.setType(SoftButtonType.SBT_TEXT);
        button.setIsHighlighted(false);
        button.setSystemAction(SystemAction.DEFAULT_ACTION);
        return button;
    }

    public static void onCustomButtomPressed(Context context, OnButtonPress notification){
        switch (notification.getCustomButtonName()) {
            case Constants.SoftButtons.SOS_BUTTON:
                Logger.d("SOS Button Pressed");
                Intent intent = new Intent(context, PanicoReceiver.class);
                context.sendBroadcast(intent);
                break;
        }
    }

}
