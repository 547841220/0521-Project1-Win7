package com.aimoney.fieldledger.win7;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

final class CsvWriter {
    private CsvWriter() {
    }

    static void write(File file, String[] headers, List<String[]> rows) throws IOException {
        TextStore.ensureParent(file);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        try {
            out.write('\ufeff');
            writeRow(out, headers);
            for (String[] row : rows) {
                writeRow(out, row);
            }
        } finally {
            out.close();
        }
    }

    private static void writeRow(BufferedWriter out, String[] values) throws IOException {
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                out.write(',');
            }
            out.write(escape(values[i]));
        }
        out.newLine();
    }

    private static String escape(String value) {
        String text = value == null ? "" : value;
        if (text.indexOf(',') >= 0 || text.indexOf('"') >= 0 || text.indexOf('\n') >= 0 || text.indexOf('\r') >= 0) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }
}
