package com.ly.ersblockgame;

import org.omg.CORBA.PUBLIC_MEMBER;

public class ErsBlock extends Thread{

    public final static int BOXS_ROWS=4;//一个块占的行数
    public final static int BOXS_COLS=4;//一个块占的列数
    //让升级变化的平滑因子，避免最后几级之间速度相差近一倍
    public final static int LEVEL_FLATNESS_GENE=3;
    //相近的两级之间，块没下落一行的时间差别为多少（ms）
    public final static int BETWEEN_LEVELS_DEGRESS_TIME=50;
    public final static int BLOCK_KIND_NUMBER=7;//方块的样式数目
    public final static int BLOCK_STATUS_NUMBER=4;//每个样式的方块反转种类
    public final static int [][] STYLES={
            {0x0f00, 0x4444, 0x0f00, 0x4444},//长条形4种状态
            {0x04e0, 0x0464, 0x00e4, 0x04c4},//‘T’型的四种状态
            {0x4620, 0x6c00, 0x4620, 0x6c00},//反‘z’型的四种状态
            {0x2640, 0xc600, 0x2640, 0xc600},//'z'型的四种状态
            {0x6220, 0x1700, 0x2230, 0x0740},//‘7’型的四种状态
            {0x6440, 0x0e20, 0x44c0, 0x8e00},//反‘7’型的四种状态
            {0x0660, 0x0660, 0x0660, 0x0660},//方块的四种状态
    };
    private GameCanvas canvas;//该方块所在的画布
    private ErsBox[][] boxs=new ErsBox[BOXS_ROWS][BOXS_COLS];//16个方格
    private int style,x,y,level;
    private boolean pausing=false,moving=true;
    //构造方法，产生一个特定的块
    public ErsBlock(int style,int y,int x,int level,GameCanvas canvas){
        this.style=style;
        this.y=y;
        this.x=x;
        this.level=level;
        this.canvas=canvas;
        int key=0x8000;
        for(int i=0;i<boxs.length;i++){
            for(int j=0;j<boxs[i].length;j++){
                boolean isColor=((style&key)!=0);
                boxs[i][j]=new ErsBox(isColor);
                key>>=1;
            }
        }
        display();
    }

    //线程类的run()方法覆盖，下落块，直到块不能再下落
    public void run(){
        while(moving){
            try {
                sleep(BETWEEN_LEVELS_DEGRESS_TIME*(ErsBlocksGame.MAX_LEVEL-level+LEVEL_FLATNESS_GENE));
            }catch (InterruptedException ie){
                ie.printStackTrace();
            }
            //moving 是表示在等待睡眠时间后，moving没有被改变
            if(!pausing) moving=(moveTo(y+1,x)&&moving);
        }
    }

    //往左移动一格
    public void moveLeft(){
        moveTo(y,x-1);
    }
    //往右移动一格
    public void moveRight(){
        moveTo(y,x+1);
    }
    //向下移动一格
    public void moveDown(){
        moveTo(y+1,x);
    }

    //块变型
    public void turnNext(){
        for(int i=0;i<BLOCK_KIND_NUMBER;i++){
            for(int j=0;j<BLOCK_STATUS_NUMBER;j++){
                if(STYLES[i][j]==style){
                    int newStyle=STYLES[i][(y+1)%BLOCK_STATUS_NUMBER];
                    turnTo(newStyle);
                    return;
                }
            }
        }
    }

    //暂停下落，对应游戏暂停
    public void pauseMove(){
        pausing=true;
    }
    //块继续下落
    public void resumeMove(){
        pausing=false;
    }
    //停止块的下落，对应游戏停止
    public void stopMove(){
        moving=false;
    }

    //将当前块从画布的对应位置抹去，等到下次重画画布时才能表现出来
    private void erase(){
        for(int i=0;i<boxs.length;i++){
            for(int j=0;j<boxs[i].length;j++){
                if(boxs[i][j].isColorBox()){
                    ErsBox box=canvas.getBox(i+y,j+x);
                    if(box==null)continue;
                    box.setColor(false);
                }
            }
        }
    }

    //将当前块从画布的对应位置显示出来，等到下次重画画布时才能表现出来
    private void display(){
        for(int i=0;i<boxs.length;i++){
            for(int j=0;j<boxs[i].length;j++){
                if(boxs[i][j].isColorBox()){
                    ErsBox box=canvas.getBox(i+y,j+x);
                    if(box==null)continue;
                    box.setColor(true);
                }
            }
        }
    }

    /**当前块能否移动到newRow/newCol 所指定的位置
     * @param newRow int 目的地所在行
     * @param newCol int 目的地所在列
     * @return boolean, true-能移动，false-不能
     */
    private boolean isMoveAble(int newRow,int newCol){
        erase();
        for(int i=0;i<boxs.length;i++){
            for(int j=0;j<boxs[i].length;j++){
                if(boxs[i][j].isColorBox()){
                    ErsBox box=canvas.getBox(i+newRow,j+newCol);
                    //如果当前位置不能时方块或是已有填充的块，则不能移动
                    if(box==null||box.isColorBox()){
                        display();
                        return false;
                    }
                }
            }
        }
        display();
        return true;
    }
    /**当前块移动到newRow/newCol 所指定的位置
     * @param newRow int 目的地所在行
     * @param newCol int 目的地所在列
     * @return boolean, true-移动成功，false-移动失败
     */
    private synchronized boolean moveTo(int newRow,int newCol){
        //如果在新的行不能移动或停止移动则返回假
        if(!isMoveAble(newRow,newCol)||!moving)return false;
        //抹掉旧的痕迹
        erase();
        //在新的位置重画
        y=newRow;
        x=newCol;
        display();
        canvas.repaint();
        return true;
    }

    /** 当前块能否变成newStyle所指定的块样式，主要是考虑边界以及被其他块挡住、不能移动的情况
     * @param newStyle int, 希望改变的样式，对应STYLES的28个值中的一个
     * @return boolean, true 能改变，false 不能改变
     */
    private boolean isTurnAble(int newStyle){
        int key=0x8000;
        erase();
        for(int i=0;i<boxs.length;i++){
            for(int j=0;j<boxs[i].length;j++){
                //检查4x4的方格内，新模式的格子是否为填充块
                if((newStyle&key)!=0){
                    //检查当前格子是否为在面板内并且不是填充块
                    //是则不能移动
                    ErsBox box=canvas.getBox(y+i,x+j);
                    if(box==null||box.isColorBox()){
                        display();
                        return false;
                    }
                }
                key>>=1;
            }
        }
        display();
        return true;
    }

    /** 将当前的块变成newStyle所指定的块的样式
     * @param newStyle int, 希望改变的样式，对应STYLES的28个值中的一个
     * @return boolean, true 改变成功，false 改变失败
     */
    private boolean turnTo(int newStyle){
        if(!isTurnAble(newStyle)||!moving) return false;
        erase();
        int key=0x8000;
        for(int i=0;i<boxs.length;i++){
            for(int j=0;j<boxs[i].length;j++){
                boolean isColor=((newStyle&key)!=0);
                boxs[i][j].setColor(isColor);
                key>>=1;
            }
        }
        style=newStyle;
        display();
        canvas.repaint();
        return true;
    }
}
