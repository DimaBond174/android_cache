package com.bond.oncache.gui;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.bond.oncache.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SpecTheme {

    //#################################################################
    /* Блок констант  - не меняются темой: */
    /* цвет текста когда он на фоне */
    public static final int PWhiteColor      = 0xffffffff;
    //public static volatile int PTextColor       = 0xffffffff;
    public static final int PTextWheat       = 0xffF5DEB3;
    public static final int PBlackColor      = 0xff000000;
    public static final int PLightGrayColor  = 0xffbdbdbd; // Фон для групп в контактах
    public static final int PGrayColor       = 0xff808080; // Даты в ленте
    public static final int PDimGrayColor    = 0xff696969; // Текст в ленте
    public static final int PDividerGrayColor = 0xff8f8f8f; //__________
    public static final int PLimeColor        = 0xff00FF00;
    public static final int PLightGreenColor  = 0xff66bb6a;
    public static final int PForestGreenColor = 0xff228B22;
    public static final int PForestGreenColorA = 0x77228B22;
    public static final int PGreenOKColor     = 0xff64dd17;
    public static final int PRedBADColor      = 0xffd50000;

    /* основная цветовая гамма */
    public static volatile int PColor           = 0xff2e7d32;
    public static volatile int PDarkColor       = 0xff005005;
    public static volatile int PLightColor      = 0xff60ad5e;
    public static volatile int PSuperLightColor = 0xffe8f5e9;

    /* вспомогательная цветовая гамма */
    public static volatile int SColor = 0xff0277bd;
    public static volatile int SDarkColor = 0xff004c8c;
    public static volatile int SLightColor = 0xff58a5f0;

    /* Шрифты */
    public static final String EmptyString = "";
    public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    public static String formateDate(long in_date) {
        return dateFormatter.format(new Date(in_date));
    }
    public static volatile boolean isRTL = false;
    /* Sizes in TypedValue.COMPLEX_UNIT_DIP */
    public static volatile int     PTextSize    = 20;
    public static volatile int     STextSize    = 18;
    public static volatile int     InfoTextSize = 14;
    public static volatile int     ButtonTextSize = 28;

    public static volatile int     PTextColor   = 0xff000000; //Чёрный
    public static volatile int     STextColor   = 0xff696969; //Тёмно серый
    public static volatile int     InfoTextColor   = 0xff808080; //Светло серый
    public static volatile int     AccentTextColor   = 0xff228B22;
    public static volatile int     WTextColor   = PTextWheat; //Цвет на тёмном фоне

    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    /* Расчётные величины */

    /* Размеры кнопок */
    public static volatile int dpMaxEmojiKeyboard = 249; //dp Макс размер клавы
    public static volatile int dpTextPadding     = 72; //dp Начало текста после иконок
    public static volatile int dpButtonTouchSize = 52; //dp Подсвечиваемая область
    //public static volatile int dpAvaIconSize     = 40; //dp Размер аватарки в сообщениях и др.
    public static volatile int dpAvaIconSize     = 50; //dp Размер аватарки в сообщениях и др.
    public static volatile int dpButtonBigImgSizeIn   = 40; //Картинка внутри кнопки
    //public static volatile int dpButtonSize20      = 20; //Полкартинки
    public static volatile int dpButtonBigImgSizeOut  = 46; //Вся кнопка
    public static volatile int dpButtonImgSize     = 36; //dp Рисованная картинка на кнопке
    public static volatile int dpButtonSmImgSize   = 24; //dp Маленькая картинка в менюхе
    public static volatile int dpButtonImgSizeHalf = 18; //Пол картинки
    public static volatile int dpButtonPadding   = 8; //dp  Отступы картинки
    public static volatile int dpButtonPadding3  = 3; //dp  Отступы картинки
    public static volatile int dpButton2Padding  = 16; //dp
    public static volatile float dpCornerR         = 10.0f; //dp
    public static volatile float dpPaintPLine      = 4.0f; //dp
    public static volatile float dpPaintLine       = 2.0f; //dp
    public static volatile float dpPaint1Line      = 1.0f; //dp

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    /* В тёмной теме не рекомендуется использовать чисто белый цвет - режет глаз
    * это блок адаптированных цветов для интерфейса */
    public static int PMsgBackColor         = 0xffffffff;
    public static int PMyMsgBackColor       = 0xffe2ffc7;
    public static int PWaitBarColor         = 0xffebeff2;
    public static int PHiBackColor          = 0xffdcedc8;
    /* фон клавиатуры снизу */
    public static volatile int KeyBoardColor = 0xffebeff2;
    /* подсветка кликов - фон */
    public static volatile int HiLightColor    = 0x77b9f6ca; //Просвечивает фон
    public static volatile int WhileTransColor = 0x99ffffff; //Просвечивает белый фон
    /* Просвечивающий фон */
    public static volatile int TranslucentColor = 0x77000000;
    public static volatile int ShadowColor = 0x11000000;
    // Замена альфы: int res = (color & 0x00ffffff) | (alpha << 24);
    //public static volatile int ShadowStart      = 77;//alpha
    public static volatile int ShadowStart      = 77;//alpha
    public static volatile int ShadowEnd        = 11;//alpha

    public static final int  color_array[] = new int[]  {
        SColor,
        PGreenOKColor,
        PRedBADColor,
        SDarkColor,
        PLightGreenColor,
        PGrayColor
    };


    /* Размеры кнопок */
    private static final int MaxEmojiKeyboard = 249; //dp Макс размер клавы
    private static final int TextPadding     = 72; //dp Начало текста после иконок
    private static final int ButtonTouchSize = 52; //dp Подсвечиваемая область
    //public static final int AvaIconSize     = 40; //dp Размер аватарки в сообщениях и др.
    private static final int AvaIconSize     = 50; //dp Размер аватарки в сообщениях и др.
    private static final int ButtonImgSize   = 36; //dp Рисованная картинка на кнопке
    private static final int ButtonBigImgSizeIn = 40;
    //private static final int ButtonSize20       = 20;
    private static final int ButtonBigImgSizeOut= 46;
    private static final int ButtonSmImgSize = 24; //Маленькая картинка в меню
    private static final int ButtonPadding   = 8; //dp  Отступы картинки
    private static final int ButtonPadding3  = 3; //dp  Отступы картинки
    private static final int Button2Padding  = 16; //dp


    /* Вспомогательное */
    public static final Paint paintLine     = new Paint();
    public static final Paint paintProgresS = new Paint();
    public static final Paint paintProgresL = new Paint();
    public static LightingColorFilter colorFilterSColor = new LightingColorFilter( 0, SColor);
    public static LightingColorFilter colorFilterPColor = new LightingColorFilter( 0, PColor);
    public static LightingColorFilter colorFilterWColor = new LightingColorFilter( 0, PWhiteColor);
    public static LightingColorFilter colorFingerColor  = new LightingColorFilter( 0, 0xab228B22);
    public static ColorMatrixColorFilter grayScaleFilter = null;//new ColorMatrixColorFilter();
    public static final LightingColorFilter greenColorFilter  = new LightingColorFilter( 0, 0xff32cb00);
    public static final LightingColorFilter yellowColorFilter = new LightingColorFilter( 0, 0xffc8b900);
    public static final LightingColorFilter grayColorFilter   = new LightingColorFilter( 0, 0xff757575);

    public static Context context = null;
    public static Resources resources = null;
//    public static Drawable empty1 = null;
//    public static Drawable sigLvl0 = null;
//    public static Drawable sigLvl1 = null;
//    public static Drawable sigLvl2 = null;
//    public static Drawable sigLvl3 = null;
//    public static Drawable[][] sigLvlArr = new Drawable[2][3];
//    public static Drawable doneOK        = null; //Зелёная галка
//    public static Drawable cancelWrong = null; //Красный крестик
//    public static Drawable fileIcon         = null; //Иконка файла
    public static Drawable play_icon      = null;
    public static Drawable stop_icon      = null;
    public static Drawable ok_icon         = null;

  //Блокиратор множественного нажатия (когда много виджетов хватаются за одно касание)
    public static long lastClickTime = 0L;

    public  static void onDestroy(){
        context  =  null;
        resources = null;
      play_icon = null;
      stop_icon = null;
      ok_icon = null;
    }

    //public static void applyMetrics(float den, Resources res){
    //public static void applyMetrics(float den, Context appContext, Context dialogCtx){
    public static void applyMetrics(Context dialogCtx)  {
        float den = dialogCtx.getResources().getDisplayMetrics().density;
        context  =  dialogCtx;
      play_icon = ContextCompat.getDrawable(context, R.drawable.ic_play_circle_outline_black_24dp);
      play_icon.setColorFilter(new LightingColorFilter( 0, 0xffffffff));
      stop_icon = ContextCompat.getDrawable(context, R.drawable.ic_stop_black_24dp);
      stop_icon.setColorFilter(new LightingColorFilter( 0, 0xffffffff));
      ok_icon = ContextCompat.getDrawable(context, R.drawable.ic_done_black_24dp);
      ok_icon.setColorFilter(new LightingColorFilter( 0, 0xffffffff));

//        resources = dialogCtx.getResources();
//        fileIcon = AppCompatResources.getDrawable(dialogCtx, R.drawable.file_icon);
//        doneOK = AppCompatResources.getDrawable(dialogCtx, R.drawable.ic_done_black_24dp);
//        doneOK.setColorFilter(new LightingColorFilter( 0, PGreenOKColor));
//        cancelWrong = AppCompatResources.getDrawable(dialogCtx, R.drawable.ic_cancel_black_24dp);
//        cancelWrong.setColorFilter(new LightingColorFilter( 0, PRedBADColor));

        SpecTheme.dpMaxEmojiKeyboard = (int) Math.ceil(SpecTheme.MaxEmojiKeyboard * den);
        SpecTheme.dpTextPadding     = (int) Math.ceil(SpecTheme.TextPadding * den);
        SpecTheme.dpButtonTouchSize = (int) Math.ceil(SpecTheme.ButtonTouchSize * den);
        SpecTheme.dpAvaIconSize     = (int) Math.ceil(SpecTheme.AvaIconSize * den);
        SpecTheme.dpButtonImgSize   = (int) Math.ceil(SpecTheme.ButtonImgSize * den);
        SpecTheme.dpButtonBigImgSizeIn = (int) Math.ceil(SpecTheme.ButtonBigImgSizeIn * den);
        //SpecTheme.dpButtonSize20       = (int) Math.ceil(SpecTheme.ButtonSize20 * den);
        SpecTheme.dpButtonBigImgSizeOut = (int) Math.ceil(SpecTheme.ButtonBigImgSizeOut * den);
        SpecTheme.dpButtonSmImgSize = (int) Math.ceil(SpecTheme.ButtonSmImgSize * den);
        SpecTheme.dpButtonImgSizeHalf = SpecTheme.dpButtonImgSize >> 1;
        SpecTheme.dpButtonPadding   = (int) Math.ceil(SpecTheme.ButtonPadding * den);
        SpecTheme.dpButtonPadding3  = (int) Math.ceil(SpecTheme.ButtonPadding3 * den);
        SpecTheme.dpButton2Padding  = SpecTheme.dpButtonPadding <<1;
        SpecTheme.dpPaintLine       = 2.0f * den;
        SpecTheme.dpCornerR         = 10.0f * den;
        SpecTheme.dpPaint1Line      = den;
        SpecTheme.dpPaintPLine      = 4.0f * den;
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(SpecTheme.dpPaintLine);
        paintLine.setColor(SpecTheme.SColor);
        paintProgresS.setStyle(Paint.Style.STROKE);
        paintProgresS.setStrokeWidth(SpecTheme.dpPaintPLine);
        paintProgresS.setColor(SpecTheme.SColor);
        paintProgresL.setStyle(Paint.Style.STROKE);
        paintProgresL.setStrokeWidth(SpecTheme.dpPaintPLine);
        paintProgresL.setColor(SpecTheme.SLightColor);
        //colorFilterSColor = new LightingColorFilter( 0, SColor);
        //colorFilterPColor = new LightingColorFilter( 0, PColor);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0.0f);
        grayScaleFilter = new ColorMatrixColorFilter(matrix);
    }

}
