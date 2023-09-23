package myproject.java.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ストリームからCSV形式のデータを読み取り、CSV１行分のデータを返すイテレータを
 * 実装します。
 * <p>
 * Copyright ycookjp
 * https://github.com/ycookjp/
 * </p>
 * <table border='1'><caption>【使用例】</caption><tr><td><pre>
 * import myproject.java.utils.CsvIterator;
 * import java.io.FileInputStream;
 * import java.io.InputStreamReader;
 * import java.io.Reader;
 * import java.io.IOException;
 * ...
 * Reader in = null;
 * try {
 *     in = new InputStreamReader(
 *             new FileInputStream("/path/to/csv"), "UTF-8");
 *     for (List&gt;String&lt; rowdata: new CsvIterator(in)) {
 *         ...
 *     }
 * } catch (IOException ie) {
 *     ...
 * } finally {
 *     if (in != null) {
 *         try {
 *             in.close();
 *         } catch (IOException ie) { }
 *     }
 * }
 * </pre></td></tr></table>
 */
public class CsvIterator implements Iterable<List<String>>, Iterator<List<String>> {

    /**
     * CSVデータを読み込むために、コンストラクタから渡された{@link Reader}より
     * 作成された{@link BufferedReader}のインスタンス。
     */
    private BufferedReader linein = null;

    /**
     * {@link #hasNext()}でストリームの最後かどうかを確認するために先読みした
     * １行の文字列を保持します。読み込んだ結果を保持しない場合はnullが 設定
     * されます。
     */
    private String strbuf = null;

    /**
     * CSV形式のデータを入力する{@link Reader}を指定して、CSV１行分のデータを
     * 返すイテレータを構築します。
     * @param in CSV形式のデータを入力する{@link Reader}
     */
    public CsvIterator(Reader in) {
        this.linein = new BufferedReader(in);
    }

    /**
     *  外部からのデフォルトコンストラクタ呼び出しを抑止するための
     *  コンストラクタ。
     */
    protected CsvIterator() { }

    /**
     * CSV１行分のCSV項目を格納知った{@link List}のイテレータを返します。
     */
    @Override
    public Iterator<List<String>> iterator() {
        return this;
    }

    /**
     * 反復処理で更にに要素がある場合にtrueを返します。
     * つまり、next()が例外をスローするのではなく要素を返す場合は、trueを
     * 返します。
     * このメソッドを呼び出すと、コンストラクタから渡された{@link Reader}の
     * {@link Reader#read()}メソッドを呼び出し、-1が返された場合はfalseを
     * 返します。そうでない場合は、{@link #strbuf}に読み込んだ文字を追加して
     * trueを返します。
     *
     * @return 次の要素がある場合はtrue、そうでない場合はfalseを返します。
     * @throws RuntimeException 内部で{@link IOException}が発生した場合。
     */
    @Override
    public boolean hasNext() {
        try {
            if (this.strbuf == null) {
                this.strbuf = this.linein.readLine();
            }
            if (this.strbuf == null) {
                return false;
            }
            return true;
        } catch (IOException ie) {
            throw new RuntimeException(ie);
        }
    }

    /**
     * CSV１行分のデータを格納した{@link List}を返します。
     * <p>
     * CSV形式の文字列からCSVの項目を要素とする{@link List}を生成して返却する
     * 処理は以下のとおりである。
     *
     * <ol>
     * <li>
     *   「"」が見つかったら次の「"」が見つかるまでコンマや改行を含めて
     *   読み込んだ文字列を現在処理中の{@link List}項目の文字列に追加する。
     * </li>
     * <li>
     *   カンマが見つかったら、現在処理中の{@link List}項目の文字列をlistに
     *   追加して、次の{@link List}項目の文字列追加処理を開始する。その際
     *   追加された{@link List}項目の文字列の先頭と最後が「"」である場合は、
     *   最初と最後の「"」を除去し、連続する２つの「"」は１つの「"」に変換する。
     * </li>
     * <li>
     *   改行またはストリームの終わりに達したら、現在処理中の{@link List}項目の
     *   文字列から最後の改行コードを除いて{@link List}に追加してそのlistを
     *   返す。なお、追加された{@link List}項目の文字列の先頭と最後が「"」の
     *   場合の扱いは、カンマが見つかった場合と同様である。
     * </li>
     * </ol>
     *
     * @return CSV１行分の各項目が格納された{@link List}を返します。
     * @throws RuntimeException 内部で{@link IOException}が発生した場合。
     */
    @Override
    public List<String> next() {
        try {
            List<String> line = readCsv(this.linein);
            return line;
        } catch (IOException ie) {
            throw new RuntimeException(ie);
        }
    }

    /**
     * {@link BufferedReader}からCSV１行分のデータを読み込み、CSVの各項目を
     * ｛@link List}に格納して返します。
     * <p></p>
     * @param in CSV
     * @return CSVの各項目を格納した{@link List}を返します。
     * @throws IOException 入力データの読み込みに失敗した場合
     */
    private List<String> readCsv(BufferedReader in) throws IOException {
        boolean continueReading = true;
        boolean inDquote = false;
        List<String> rowdata = new ArrayList<String>();
        // CSV項目の初期化する
        StringBuilder csvcol = new StringBuilder();

        while (continueReading) {
            String line = readLine();
            if (line == null) {
                if (csvcol.length() > 0) {
                    rowdata.add(trimDoubleQuote(csvcol.toString()));
                }
                break;
            } else if (!inDquote && line.length() == 0) {
                break;
            }
            int index = 0;
            // １行の文字列を順に調べる
            while (index < line.length()) {
                // ダブルクォートの中である場合
                if (inDquote) {
                    // 次のダブルクォートの出現位置を取得
                    int dqidx = line.indexOf('"', index);
                    // 次のダブルクォートが見つからない場合は改行を含む行末までの
                    // 文字列をセルの文字に追加して、次の行を読み込む
                    if (dqidx < 0) {
                        csvcol.append(line.substring(index));
                        index = line.length();
                    } else {
                        // 次のダブルクォートの文字が見つかったらそこまでの文字列をセルの
                        // 文字に追加して、それ以降の文字を処理する
                        csvcol.append(line.substring(index, dqidx + 1));
                        index = dqidx + 1;
                        inDquote = false;
                    }
                } else {
                    // 次のダブルクォート、カンマの出現位置を取得
                    int dqidx = line.indexOf('"', index);
                    int cmidx = line.indexOf(',', index);
                    if (dqidx >= 0 && (cmidx < 0 || dqidx < cmidx)) {
                        // ダブルクォートの前にコンマが存在しない場合
                        // ダブルクォートまでをセルの文字列に追加する
                        csvcol.append(line.substring(index, dqidx + 1));
                        inDquote = true;
                        index = dqidx + 1;
                    } else if (cmidx >= 0) {
                        // コンマの前にダブルクォートが存在しない場合
                        // コンマの前までの文字列をセルの文字列に追加し、次のセルの処理を開始
                        csvcol.append(line.substring(index, cmidx));
                        rowdata.add(trimDoubleQuote(csvcol.toString()));
                        csvcol.delete(0, csvcol.length());
                        index = cmidx + 1;
                    } else {
                        // コンマもダブルクォートも存在しない場合
                        // 行末までの文字をセルの文字列に追加し、１行分のCSVデータを返す
                        csvcol.append(line.substring(index));
                        index = line.length();
                    }
                }
                if (index >= line.length() && inDquote) {
                    csvcol.append(System.lineSeparator());
                } else if (index >= line.length()) {
                    rowdata.add(trimDoubleQuote(csvcol.toString()));
                    continueReading = false;
                }
            }
        }
        return rowdata;
    }

    /**
     * 文字列の両端がダブルクォートの場合、両端のダブルクォートを削除して
     * 更に「""」を「"」に置換します。
     * @param str 変換元の文字列
     * @return 変換結果の文字列を返します。
     */
    private String trimDoubleQuote(String str) {
        String replaced = str;
        if (str != null && str.length() > 1 && str.charAt(0) == '"'
                && str.charAt(str.length() - 1) == '"') {
            // 文字列の開始、終了文字が共にダブルクォートの場合
            replaced = str.substring(1, str.length() - 1);
            replaced = replaced.replace("\"\"", "\"");
        }
        return replaced;
    }

    /**
     * CSVを入力する{@link Reader}から１行を読み込みます。
     *
     * {@link #hasNext()}で先読みした１行の文字列があればそれを帰します。
     * 先読みしたデータがなければCSVを入力する{@link Reader}から１行を読み込み
     * その文字列を帰します。
     * @return CSVを入力する[@link Reader}から読み込んだ１行の文字列を帰します。
     *      返却される文字列に改行は含みません。
     * @throws IOException
     */
    private String readLine() throws IOException {
        String line = this.strbuf;
        if (line == null) {
            line = this.linein.readLine();
        }
        this.strbuf = null;
        return line;
    }
}
