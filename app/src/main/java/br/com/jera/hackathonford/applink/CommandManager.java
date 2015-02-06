package br.com.jera.hackathonford.applink;

import android.content.Context;
import android.content.Intent;

import com.ford.syncV4.exception.SyncException;
import com.ford.syncV4.proxy.SyncProxyALM;
import com.ford.syncV4.proxy.rpc.OnCommand;

import java.util.List;
import java.util.Vector;

import br.com.jera.hackathonford.applink.commands.SOS;
import br.com.jera.hackathonford.receiver.PanicoReceiver;
import br.com.jera.hackathonford.utils.Constants;
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

    public static int addVoiceCommands(SyncProxyALM proxy, int autoIncCorrId) {
        if(proxy!=null){
            try {
                autoIncCorrId = addCommand(Constants.VoiceCommands.SOS_COMMAND_ID, SOS.values(), proxy, autoIncCorrId);
            } catch (SyncException e) {
                e.printStackTrace();
            }

        }
        return autoIncCorrId;
    }

    public static int addCommand(int commandId, Enum[] values, SyncProxyALM proxy, int autoIncCorrId) throws SyncException {
        if(values!=null && values.length > 0 && proxy!=null){
            Vector<String> vector = new Vector<>();
            String name = null;
            for(Enum value : values){
                vector.add(value.name());
                if(name==null){
                    name = value.name();
                }
            }
            proxy.addCommand(commandId, name, vector, autoIncCorrId++);
        }
        return autoIncCorrId;
    }
}
