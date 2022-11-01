package com.adobe.dx.aep.poc.uis.ces;

import com.adobe.dx.aep.skaluskar.poc.ces.types.ThreadContext;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * @author agarwalankit
 *
 */
@ToString
@Data
public class GraphState {
    //. separated list of successfully completed ops
    private String status = "";

    public static void updateStatus(ThreadContext threadCtx, GraphState state){
        if(threadCtx == null) {
            return;
        }
        String status = state.getStatus();
        if(status.contains(threadCtx.getThreadNo()+"")) return; // status already added
        if(!StringUtils.isEmpty(status)) status += ".";
        status += threadCtx.getThreadNo();
        state.setStatus(status);
    }
}
