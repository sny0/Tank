import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// fpsを実現する為に参考にしたサイト
// http://javaappletgame.blog34.fc2.com/blog-entry-265.html

public class TankGame extends Frame implements Runnable{
  long error = 0;// 休止時間の誤差
  int fps = 30; // frame per second
  long idealSleep = (1000 << 16) / fps;
  long oldTime;// 前の時間
  long newTime;// 後の時間
  long sleepTime;// 休止する時間
  Thread th;
  GameMaster gm;
  private int cW, cH;// 縦横

  public static void main(String[] args){
   new TankGame(); // ゲーム画面
  }

  TankGame(){// コンストラクタ

    //フレームの設定
    super("Tank");// スレッド名を指定
    cW = 1328;
    cH = 848;
    this.setSize(cW+20, cH+40);
    this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

    //GameMasterインスタンスの作成
    gm = new GameMaster(cW, cH, this);
    this.add(gm);
    this.setVisible(true);

    //スレッドの作成
    th = new Thread(this);
    th.start();//スレッド開始

    requestFocusInWindow();//フォーカスを要求

    //ウィンドウを閉じるときに呼ばれる処理
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
          dispose(); // ウィンドウを破棄する
          System.exit(0); // プログラムを終了する
      }
    });
  }


  public void run(){
    try{
      while (true){
        /*
        計算の前の時間と後の時間を保持しその差からfpsを実現するためのスリープタイムを計算しスリープする
        */
        oldTime = newTime;
        gm.repaint(); // 計算と描画
        sleepTime = idealSleep - (newTime - oldTime) - error;
        if(sleepTime < 0x20000) sleepTime = 0x20000;// 最低でも2msは休止
        oldTime = newTime;
        Thread.sleep(sleepTime >> 16);// 休止
        newTime = System.currentTimeMillis() << 16;
        error = newTime - oldTime - sleepTime;// 休止時間の誤差
        //System.out.println("error"+error);
        //System.out.println(newTime-oldTime);
        //System.out.println(1000 << 16);
        //System.out.println(sleepTime);
        //Thread.sleep(sleepTime >> 16);
        //System.out.println(((System.currentTimeMillis() << 16)-oldTime) * fps);
      }
    }
    catch(Exception e){
      System.out.println("Exception: "+ e);
    }
  }
}
