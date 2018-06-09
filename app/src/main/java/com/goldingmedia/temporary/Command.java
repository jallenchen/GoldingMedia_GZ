package com.goldingmedia.temporary;

import com.goldingmedia.most.fblock.FBlock;

public class Command {

    public static void sendCommandString(String sendString){
    	FBlock.GetInstance().BoardcastSet.Set(sendString.trim()+"%#%");
    }
}
