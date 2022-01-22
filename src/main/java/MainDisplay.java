import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Arrays;

public class MainDisplay extends Display{

    Display draw;
    static AI ai;

    MainDisplay(){
        System.out.println("MainDisplay_class start");
        this.draw = new Draw();
        this.current = this.draw;
        try {
            this.ai = new AI();
            this.ai.start();
        }catch (IOException | InvalidKerasConfigurationException | UnsupportedKerasConfigurationException e){
            System.err.println(e.getMessage());
        }

    }

    private class Draw extends Display{

        //キャンバスサイズ
        int canvasSize = 560;
        int infoWidth = 190;
        //左上座標
        int boardX = 20;
        int boardY = 55;

        @Override
        public void show(GraphicsInfo ginfo) {
            ginfo.g.setBackground(Color.blue);
            ginfo.g.clearRect(0,0,(int)ginfo.windowWidth, (int)ginfo.windowHeight);
            drawCanvas(ginfo);
            canvas(ginfo);
            infoView(ginfo);
            reset(ginfo);
        }

        @Override
        public void loadMedia() throws IOException {

        }

        //カーソル座標の取得
        int[] cursorPlace(GraphicsInfo ginfo){
            for (int i = 0; i < MainDisplay.ai.imageSize; i++){
                for (int j = 0; j < MainDisplay.ai.imageSize; j++){
                    float lX = 20 + (canvasSize / MainDisplay.ai.imageSize) * i;
                    float rX = 20 + (canvasSize / MainDisplay.ai.imageSize) * (i + 1);
                    float uY = 50 + (canvasSize / MainDisplay.ai.imageSize) * j;
                    float dY = 50 + (canvasSize / MainDisplay.ai.imageSize) * (j + 1);
                    if (ginfo.drawX > lX && ginfo.drawX < rX && ginfo.drawY > uY && ginfo.drawY < dY){
                        return new int[]{i,j};
                    }
                }
            }
            return new int[]{-100,-100};
        }

        //キャンバスを描く
        void canvas(GraphicsInfo ginfo){
            int[] places = cursorPlace(ginfo);
            //ボードの下地
            for (int i = 0; i < MainDisplay.ai.imageSize; i++){
                for (int j = 0; j < MainDisplay.ai.imageSize; j++){
                    float lX = boardX + (this.canvasSize / MainDisplay.ai.imageSize) * i;
                    float rX = boardX + (this.canvasSize / MainDisplay.ai.imageSize) * (i + 1);
                    float uY = boardY + (this.canvasSize / MainDisplay.ai.imageSize) * j;
                    float dY = boardY + (this.canvasSize / MainDisplay.ai.imageSize) * (j + 1);
                    if ((ginfo.cursorX > lX && ginfo.cursorX < rX && ginfo.cursorY > uY && ginfo.cursorY < dY) || MainDisplay.ai.image[i][j] == 1){
                        ginfo.g.setColor(new Color(0,0,0));
                    }
                    else{
                        ginfo.g.setColor(new Color(255,255,255));
                    }
                    //中心座標
                    Rectangle2D.Double place = new Rectangle2D.Double(lX,uY,rX-lX,dY-uY);
                    ginfo.g.fill(place);
                }
            }
        }

        boolean drawCanvasOk(int x,int y){
            if(-1<x && x<MainDisplay.ai.imageSize && -1<y && y<MainDisplay.ai.imageSize){
                return true;
            }
            else{return false;}
        }
        //キャンバスに描く
        void drawCanvas(GraphicsInfo ginfo){
            int[] place = cursorPlace(ginfo);
            for (int i=-1;i<2;i++){
                for (int j=-1;j<2;j++){
                    if(drawCanvasOk(place[0]+i, place[1]+j)){
                        MainDisplay.ai.image[place[0]+i][place[1]+j] = 1;
                    }
                }
            }
        }

        void infoView(GraphicsInfo ginfo){
            //情報表示用の枠1
            ginfo.g.setColor(Color.WHITE);
            Rectangle2D.Double block = new Rectangle2D.Double(canvasSize + boardX + 10, boardY, infoWidth,300);
            ginfo.g.fill(block);

            ginfo.g.setFont(new Font("Sanserif", Font.BOLD, 20));
            FontMetrics fm = ginfo.g.getFontMetrics();
            float strh = fm.getHeight();
            ginfo.g.setColor(Color.BLACK);

            String str = "現在のAIの評価(%)";
            ginfo.g.drawString(str, canvasSize + boardX + 15, boardY + strh);

            for(int i=0;i<10;i++){
                str = String.format("%d",i) + " : " + String.format("%.1f", MainDisplay.ai.score[i] * 100) + "%";
                ginfo.g.drawString(str, canvasSize + boardX + 15, boardY + strh * (i + 2));
            }

            //情報表示用の枠2
            ginfo.g.setColor(Color.WHITE);
            Rectangle2D.Double block2 = new Rectangle2D.Double(canvasSize + boardX + 10, boardY + 310, infoWidth,120);
            ginfo.g.fill(block2);

            ginfo.g.setColor(Color.BLACK);
            str = "現在のAIの推論結果";
            ginfo.g.drawString(str, canvasSize + boardX + 15, boardY + 310 + strh);//maxScoreIndex

            ginfo.g.setFont(new Font("Sanserif", Font.BOLD, 80));
            fm = ginfo.g.getFontMetrics();
            strh = fm.getHeight();
            str = String.format("%d", MainDisplay.ai.maxScoreIndex());
            ginfo.g.drawString(str, canvasSize + boardX + 85, boardY + 310 + strh);
        }

        void reset(GraphicsInfo ginfo){
            String str = "リセット";
            int fontSize = 40;
            Font mfont = new Font("Sanserif", Font.BOLD, fontSize);
            ginfo.g.setFont(mfont);

            int boxWidth = infoWidth;
            int boxHeight = 70;
            //左上座標
            float boxX = canvasSize + boardX + 10;
            float boxY = boardY + canvasSize - boxHeight;

            FontMetrics fm = ginfo.g.getFontMetrics();
            float strw = fm.stringWidth(str) / 2;
            float strh = fm.getHeight();

            if (ginfo.cursorX>boxX & ginfo.cursorX<boxX + boxWidth & ginfo.cursorY > boxY & ginfo.cursorY < boxY + boxHeight){
                ginfo.g.setColor(Color.YELLOW);
            }
            else{
                ginfo.g.setColor(Color.WHITE);
            }
            Rectangle2D.Double play1 = new Rectangle2D.Double(boxX,boxY, boxWidth,boxHeight);
            ginfo.g.fill(play1);

            ginfo.g.setColor(Color.BLACK);
            ginfo.g.drawString(str, boxX+boxWidth/2 - strw, boxY + boxHeight/2 + strh / 4);

            if (ginfo.clickX>boxX&ginfo.clickX<boxX+boxWidth&ginfo.clickY>boxY&ginfo.clickY<boxY+boxHeight){
                ginfo.clickX = -100;
                ginfo.clickY = -100;
                MainDisplay.ai.reset();
            }
        }
    }

    @Override
    public void show(GraphicsInfo ginfo) {}

    @Override
    public void loadMedia() throws IOException {}
}
