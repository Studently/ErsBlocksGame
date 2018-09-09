package com.ly.ersblockgame;

import java.awt.*;

public class ErsBox implements Cloneable{
    private boolean isColor;
    private Dimension size =new Dimension();
    /**�����๹�췽��
     * @param isColor ��ʾ�ǲ���ʹ��ǰ��ɫ��Ϊ������ɫ
     *                true ǰ��ɫ��false ����ɫ
     */
    public ErsBox(boolean isColor){
        this.isColor=isColor;
    }

    /**�˷����Ƿ�ʹ��ǰ��ɫ
     * @return boolean ,true��ǰ��ɫ��false�ñ���ɫ
     */
    public boolean isColorBox(){
        return isColor;
    }

    //���÷�����ɫ
    public void setColor(boolean isColor){
        this.isColor=isColor;
    }
    //��÷����С
    public Dimension getSize(){
        return size;
    }

    //���÷���Ĵ�С
    public void setSize(Dimension size){
        this.size=size;
    }

    /** ����objec��objec clone������ʵ�ֿ�¡
     * @return object ��¡���
     */
    public Object clone(){
        Object cloned=null;
        try {
            cloned=super.clone();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return cloned;
    }

}
