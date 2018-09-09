package com.ly.ersblockgame;

import org.omg.CORBA.PUBLIC_MEMBER;

public class ErsBlock extends Thread{

    public final static int BOXS_ROWS=4;//һ����ռ������
    public final static int BOXS_COLS=4;//һ����ռ������
    //�������仯��ƽ�����ӣ�������󼸼�֮���ٶ�����һ��
    public final static int LEVEL_FLATNESS_GENE=3;
    //���������֮�䣬��û����һ�е�ʱ����Ϊ���٣�ms��
    public final static int BETWEEN_LEVELS_DEGRESS_TIME=50;
    public final static int BLOCK_KIND_NUMBER=7;//�������ʽ��Ŀ
    public final static int BLOCK_STATUS_NUMBER=4;//ÿ����ʽ�ķ��鷴ת����
    public final static int [][] STYLES={
            {0x0f00, 0x4444, 0x0f00, 0x4444},//������4��״̬
            {0x04e0, 0x0464, 0x00e4, 0x04c4},//��T���͵�����״̬
            {0x4620, 0x6c00, 0x4620, 0x6c00},//����z���͵�����״̬
            {0x2640, 0xc600, 0x2640, 0xc600},//'z'�͵�����״̬
            {0x6220, 0x1700, 0x2230, 0x0740},//��7���͵�����״̬
            {0x6440, 0x0e20, 0x44c0, 0x8e00},//����7���͵�����״̬
            {0x0660, 0x0660, 0x0660, 0x0660},//���������״̬
    };
    private GameCanvas canvas;//�÷������ڵĻ���
    private ErsBox[][] boxs=new ErsBox[BOXS_ROWS][BOXS_COLS];//16������
    private int style,x,y,level;
    private boolean pausing=false,moving=true;
    //���췽��������һ���ض��Ŀ�
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

    //�߳����run()�������ǣ�����飬ֱ���鲻��������
    public void run(){
        while(moving){
            try {
                sleep(BETWEEN_LEVELS_DEGRESS_TIME*(ErsBlocksGame.MAX_LEVEL-level+LEVEL_FLATNESS_GENE));
            }catch (InterruptedException ie){
                ie.printStackTrace();
            }
            //moving �Ǳ�ʾ�ڵȴ�˯��ʱ���movingû�б��ı�
            if(!pausing) moving=(moveTo(y+1,x)&&moving);
        }
    }

    //�����ƶ�һ��
    public void moveLeft(){
        moveTo(y,x-1);
    }
    //�����ƶ�һ��
    public void moveRight(){
        moveTo(y,x+1);
    }
    //�����ƶ�һ��
    public void moveDown(){
        moveTo(y+1,x);
    }

    //�����
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

    //��ͣ���䣬��Ӧ��Ϸ��ͣ
    public void pauseMove(){
        pausing=true;
    }
    //���������
    public void resumeMove(){
        pausing=false;
    }
    //ֹͣ������䣬��Ӧ��Ϸֹͣ
    public void stopMove(){
        moving=false;
    }

    //����ǰ��ӻ����Ķ�Ӧλ��Ĩȥ���ȵ��´��ػ�����ʱ���ܱ��ֳ���
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

    //����ǰ��ӻ����Ķ�Ӧλ����ʾ�������ȵ��´��ػ�����ʱ���ܱ��ֳ���
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

    /**��ǰ���ܷ��ƶ���newRow/newCol ��ָ����λ��
     * @param newRow int Ŀ�ĵ�������
     * @param newCol int Ŀ�ĵ�������
     * @return boolean, true-���ƶ���false-����
     */
    private boolean isMoveAble(int newRow,int newCol){
        erase();
        for(int i=0;i<boxs.length;i++){
            for(int j=0;j<boxs[i].length;j++){
                if(boxs[i][j].isColorBox()){
                    ErsBox box=canvas.getBox(i+newRow,j+newCol);
                    //�����ǰλ�ò���ʱ��������������Ŀ飬�����ƶ�
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
    /**��ǰ���ƶ���newRow/newCol ��ָ����λ��
     * @param newRow int Ŀ�ĵ�������
     * @param newCol int Ŀ�ĵ�������
     * @return boolean, true-�ƶ��ɹ���false-�ƶ�ʧ��
     */
    private synchronized boolean moveTo(int newRow,int newCol){
        //������µ��в����ƶ���ֹͣ�ƶ��򷵻ؼ�
        if(!isMoveAble(newRow,newCol)||!moving)return false;
        //Ĩ���ɵĺۼ�
        erase();
        //���µ�λ���ػ�
        y=newRow;
        x=newCol;
        display();
        canvas.repaint();
        return true;
    }

    /** ��ǰ���ܷ���newStyle��ָ���Ŀ���ʽ����Ҫ�ǿ��Ǳ߽��Լ��������鵲ס�������ƶ������
     * @param newStyle int, ϣ���ı����ʽ����ӦSTYLES��28��ֵ�е�һ��
     * @return boolean, true �ܸı䣬false ���ܸı�
     */
    private boolean isTurnAble(int newStyle){
        int key=0x8000;
        erase();
        for(int i=0;i<boxs.length;i++){
            for(int j=0;j<boxs[i].length;j++){
                //���4x4�ķ����ڣ���ģʽ�ĸ����Ƿ�Ϊ����
                if((newStyle&key)!=0){
                    //��鵱ǰ�����Ƿ�Ϊ������ڲ��Ҳ�������
                    //�������ƶ�
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

    /** ����ǰ�Ŀ���newStyle��ָ���Ŀ����ʽ
     * @param newStyle int, ϣ���ı����ʽ����ӦSTYLES��28��ֵ�е�һ��
     * @return boolean, true �ı�ɹ���false �ı�ʧ��
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
