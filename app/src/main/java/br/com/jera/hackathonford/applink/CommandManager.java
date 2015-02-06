package br.com.jera.hackathonford.applink;

import android.content.Context;
import android.content.Intent;

import com.ford.syncV4.proxy.rpc.OnCommand;

import br.com.jera.hackathonford.receiver.PanicoReceiver;
import br.com.jera.hackathonford.utils.Logger;

/**
 * Created by marco on 05/02/15.
 */
public class CommandManager {

    public static void handleCommand(Context context, OnCommand command){
        if(command!=null){
            Integer commandId = command.getCmdID();
            Logger.d("Voice command called with id: " + commandId);
            if(commandId.equals(60)){
                Logger.d("## SOS COMAND");
                Intent intent = new Intent(context, PanicoReceiver.class);
                context.sendBroadcast(intent);
            }
        }
    }

}
