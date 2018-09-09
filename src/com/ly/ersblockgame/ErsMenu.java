package com.ly.ersblockgame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ErsMenu extends Frame {
    MenuBar bar=new MenuBar();
    //菜单条包括四个菜单
    Menu mGame=new Menu("游戏");
    Menu mControl=new Menu("控制");
    Menu mWindowStyle=new Menu("窗口风格");
    Menu mHelp=new Menu("帮助");

    //4个菜单中分别包含的菜单项
    MenuItem miNewGame=new MenuItem("新游戏");
    MenuItem miSetBlockColor=new MenuItem("设置方块颜色");
    MenuItem miSetBackColor=new MenuItem("设置背景颜色");
    MenuItem miTurnHarder=new MenuItem("增加难度");
    MenuItem miTurnEasier=new MenuItem("降低难度");
    MenuItem miExit=new MenuItem("退出");
    MenuItem miPlay=new MenuItem("开始");
    MenuItem miPasue=new MenuItem("暂停");
    MenuItem miResume=new MenuItem("继续");
    MenuItem miStop=new MenuItem("停止");
    MenuItem miAuthor=new MenuItem("作者：Java游戏设计组");
    MenuItem miSourceInfo=new MenuItem("版本：1.0");

    //设置窗口风格的菜单
    CheckboxMenuItem miAsWindows=new CheckboxMenuItem("Windows");
    CheckboxMenuItem miAsMotif=new CheckboxMenuItem("Motif");
    CheckboxMenuItem miAsMetal=new CheckboxMenuItem("Metal",true);

    //构造函数
    ErsMenu(String name){
        super(name);
        createMenu();
    }
    void createMenu(){
        bar.add(mGame);
        bar.add(mControl);
        bar.add(mWindowStyle);
        bar.add(mHelp);

        mGame.add(miNewGame);
        mGame.addSeparator();
        mGame.add(miSetBlockColor);
        mGame.add(miSetBackColor);
        mGame.addSeparator();
        mGame.add(miTurnHarder);
        mGame.add(miTurnEasier);
        mGame.addSeparator();
        mGame.add(miExit);

        mControl.add(miPlay);
        mControl.add(miPasue);
        mControl.add(miResume);
        mControl.add(miStop);

        mWindowStyle.add(miAsWindows);
        mWindowStyle.add(miAsMotif);
        mWindowStyle.add(miAsMetal);

        mHelp.add(miAuthor);
        mHelp.add(miSourceInfo);

        //将菜单挂到窗口上
        setMenuBar(bar);
        //监听退出
        miExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        //创建该类的一个对象
        ErsMenu menu=new ErsMenu("俄罗斯方块的菜单显示");
        //初始窗口的大小
        menu.setSize(500,200);
        Dimension scrSize=Toolkit.getDefaultToolkit().getScreenSize();
        //将显示窗口置于屏幕中央
        menu.setLocation((scrSize.width-menu.getSize().width)/2,(scrSize.height-menu.getSize().height)/2);
        //将窗口设置成不可编辑大小
        menu.setResizable(false);
        menu.setVisible(true);
    }
}
