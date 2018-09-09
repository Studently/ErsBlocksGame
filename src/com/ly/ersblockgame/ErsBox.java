package com.ly.ersblockgame;

import java.awt.*;

public class ErsBox implements Cloneable{
    private boolean isColor;
    private Dimension size =new Dimension();
    /**方格类构造方法
     * @param isColor 表示是不是使用前景色来为方格着色
     *                true 前景色，false 背景色
     */
    public ErsBox(boolean isColor){
        this.isColor=isColor;
    }

    /**此方格是否使用前景色
     * @return boolean ,true用前景色，false用背景色
     */
    public boolean isColorBox(){
        return isColor;
    }

    //设置方格颜色
    public void setColor(boolean isColor){
        this.isColor=isColor;
    }
    //获得方格大小
    public Dimension getSize(){
        return size;
    }

    //设置方格的大小
    public void setSize(Dimension size){
        this.size=size;
    }

    /** 覆盖objec的objec clone（），实现克隆
     * @return object 克隆结果
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
