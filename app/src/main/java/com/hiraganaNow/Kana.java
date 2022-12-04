package com.hiraganaNow;

import java.util.ArrayList;
import java.util.List;

public class Kana {
    static List<Kana> fullList;

    String character;
    String romaji;
    boolean isNewToPlayer = true;

    static boolean isValidRomaji(String romaji){
        for(Kana hiragana : fullList){
            if(hiragana.romaji.equals(romaji)){
                return true;
            }
        }
        return false;
    }

    public static void load(Game.Mode mode) {
        switch (mode) {
            case HIRAGANA:
                loadHiragana();
                break;
            case KATAKANA:
                loadKatakana();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    static void loadHiragana(){
        String dataString =
                "a:あ:"+
                "i:い:"+
                "u:う:"+
                "e:え:"+
                "o:お:"+
                "ka:か:"+
                "ki:き:"+
                "ku:く:"+
                "ke:け:"+
                "ko:こ:"+
                "ga:が:"+
                "gi:ぎ:"+
                "gu:ぐ:"+
                "ge:げ:"+
                "go:ご:"+
                "sa:さ:"+
                "shi:し:"+
                "su:す:"+
                "se:せ:"+
                "so:そ:"+
                "za:ざ:"+
                "ji:じ:"+
                "zu:ず:"+
                "ze:ぜ:"+
                "zo:ぞ:"+
                "ta:た:"+
                "chi:ち:"+
                "tsu:つ:"+
                "te:て:"+
                "to:と:"+
                "da:だ:"+
                "dji:ぢ:"+
                "dzu:づ:"+
                "de:で:"+
                "do:ど:"+
                "na:な:"+
                "ni:に:"+
                "nu:ぬ:"+
                "ne:ね:"+
                "no:の:"+
                "ha:は:"+
                "hi:ひ:"+
                "fu:ふ:"+
                "he:へ:"+
                "ho:ほ:"+
                "ba:ば:"+
                "bi:び:"+
                "bu:ぶ:"+
                "be:べ:"+
                "bo:ぼ:"+
                "pa:ぱ:"+
                "pi:ぴ:"+
                "pu:ぷ:"+
                "pe:ぺ:"+
                "po:ぽ:"+
                "ma:ま:"+
                "mi:み:"+
                "mu:む:"+
                "me:め:"+
                "mo:も:"+
                "ya:や:"+
                "yu:ゆ:"+
                "yo:よ:"+
                "ra:ら:"+
                "ri:り:"+
                "ru:る:"+
                "re:れ:"+
                "ro:ろ:"+
                "wa:わ:"+
                "wo:を:"+
                "n:ん:";
        loadCharacterData(dataString.split(":"));
    }

    static void loadKatakana() {
        String dataString =
                "a:ア:"+
                "i:イ:"+
                "u:ウ:"+
                "e:エ:"+
                "o:オ:"+
                "ka:カ:"+
                "ki:キ:"+
                "ku:ク:"+
                "ke:ケ:"+
                "ko:コ:"+
                "ga:ガ:"+
                "gi:ギ:"+
                "gu:グ:"+
                "ge:ゲ:"+
                "go:ゴ:"+
                "sa:サ:"+
                "shi:シ:"+
                "su:ス:"+
                "se:セ:"+
                "so:ソ:"+
                "za:ザ:"+
                "ji:ジ:"+
                "zu:ズ:"+
                "ze:ゼ:"+
                "zo:ゾ:"+
                "ta:タ:"+
                "chi:チ:"+
                "tsu:ツ:"+
                "te:テ:"+
                "to:ト:"+
                "da:ダ:"+
                "dji:ヂ:"+
                "dzu:ヅ:"+
                "de:デ:"+
                "do:ド:"+
                "na:ナ:"+
                "ni:ニ:"+
                "nu:ヌ:"+
                "ne:ネ:"+
                "no:ノ:"+
                "ha:ハ:"+
                "hi:ヒ:"+
                "fu:フ:"+
                "he:ヘ:"+
                "ho:ホ:"+
                "ba:バ:"+
                "bi:ビ:"+
                "bu:ブ:"+
                "be:ベ:"+
                "bo:ボ:"+
                "pa:パ:"+
                "pi:ピ:"+
                "pu:プ:"+
                "pe:ペ:"+
                "po:ポ:"+
                "ma:マ:"+
                "mi:ミ:"+
                "mu:ム:"+
                "me:メ:"+
                "mo:モ:"+
                "ya:ヤ:"+
                "yu:ユ:"+
                "yo:ヨ:"+
                "ra:ラ:"+
                "ri:リ:"+
                "ru:ル:"+
                "re:レ:"+
                "ro:ロ:"+
                "wa:ワ:"+
                "wo:ヲ:"+
                "n:ン:";
        loadCharacterData(dataString.split(":"));
    }

    private static void loadCharacterData(String[] data){
        fullList = new ArrayList<Kana>();
        for(int i = 0; i < data.length; i += 2){
            fullList.add(new Kana(data[i], data[i+1]));
        }
    }

    public Kana(String romaji, String hiragana) {
        this.character = hiragana;
        this.romaji = romaji;
    }
}
