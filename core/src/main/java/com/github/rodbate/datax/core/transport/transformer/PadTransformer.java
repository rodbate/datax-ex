package com.github.rodbate.datax.core.transport.transformer;

import com.github.rodbate.datax.common.element.Column;
import com.github.rodbate.datax.common.element.Record;
import com.github.rodbate.datax.common.element.StringColumn;
import com.github.rodbate.datax.common.exception.DataXException;
import com.github.rodbate.datax.transformer.Transformer;

import java.util.Arrays;

/**
 * no comments.
 * Created by liqiang on 16/3/4.
 */
public class PadTransformer extends Transformer {
    public PadTransformer() {
        setTransformerName("dx_pad");
    }

    @Override
    public Record evaluate(Record record, Object... paras) {

        int columnIndex;
        String padType;
        int length;
        String padString;

        try {
            if (paras.length != 4) {
                throw new RuntimeException("dx_pad paras must be 4");
            }

            columnIndex = (Integer) paras[0];
            padType = (String) paras[1];
            length = Integer.valueOf((String) paras[2]);
            padString = (String) paras[3];
        } catch (Exception e) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_ILLEGAL_PARAMETER, "paras:" + Arrays.asList(paras).toString() + " => " + e.getMessage());
        }

        Column column = record.getColumn(columnIndex);

        try {
            String oriValue = column.asString();

            //如果字段为空，作为空字符串处理
            if(oriValue==null){
                oriValue = "";
            }
            String newValue;
            if (!"r".equalsIgnoreCase(padType) && !"l".equalsIgnoreCase(padType)) {
                throw new RuntimeException(String.format("dx_pad first para(%s) support l or r", padType));
            }
            if (length <= oriValue.length()) {
                newValue = oriValue.substring(0, length);
            } else {

                newValue = doPad(padType, oriValue, length, padString);
            }

            record.setColumn(columnIndex, new StringColumn(newValue));

        } catch (Exception e) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_RUN_EXCEPTION, e.getMessage(),e);
        }
        return record;
    }

    private String doPad(String padType, String oriValue, int length, String padString) {
        StringBuilder finalPad = new StringBuilder();
        int needlength = length - oriValue.length();
        while (needlength > 0) {
            if (needlength >= padString.length()) {
                finalPad.append(padString);
                needlength -= padString.length();
            } else {
                finalPad.append(padString, 0, needlength);
                needlength = 0;
            }
        }

        if ("l".equalsIgnoreCase(padType)) {
            return finalPad.append(oriValue).toString();
        } else {
            return finalPad.insert(0, oriValue).toString();
        }
    }

}
