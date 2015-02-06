package br.com.jera.hackathonford.applink.commands;

import android.support.annotation.StringRes;

import br.com.jera.hackathonford.R;

/**
 * Created by marco on 06/02/15.
 */
public enum SOS {
    SOS(R.string.sos_command),
    SOCORRO(R.string.socorro_command),
    PANICO(R.string.panico_command),
    BOTAO_PANICO(R.string.botao_panico_command),
    EMERGENCIA(R.string.emergencia_command);

    @StringRes
    private final int title;

    private SOS(@StringRes int title){
        this.title = title;
    }
}
