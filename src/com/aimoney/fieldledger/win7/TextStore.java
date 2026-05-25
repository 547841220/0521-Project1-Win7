package com.aimoney.fieldledger.win7;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

final class TextStore {
    interface RowReader<T> {
        T read(String[] values);
    }

    interface RowWriter<T> {
        String[] write(T value);
    }

    private TextStore() {
    }

    static <T> List<T> readRows(File file, RowReader<T> reader) throws IOException {
        List<T> rows = new ArrayList<T>();
        if (!file.exists()) {
            return rows;
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                rows.add(reader.read(decodeLine(line)));
            }
        } finally {
            in.close();
        }
        return rows;
    }

    static <T> void writeRows(File file, List<T> rows, RowWriter<T> writer) throws IOException {
        ensureParent(file);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        try {
            for (T row : rows) {
                out.write(encodeLine(writer.write(row)));
                out.newLine();
            }
        } finally {
            out.close();
        }
    }

    static void ensureParent(File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("无法创建数据目录: " + parent.getAbsolutePath());
        }
    }

    private static String encodeLine(String[] values) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                builder.append('\t');
            }
            String value = values[i] == null ? "" : values[i];
            builder.append(Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8)));
        }
        return builder.toString();
    }

    private static String[] decodeLine(String line) {
        String[] cells = line.split("\\t", -1);
        String[] values = new String[cells.length];
        for (int i = 0; i < cells.length; i++) {
            values[i] = new String(Base64.getDecoder().decode(cells[i]), StandardCharsets.UTF_8);
        }
        return values;
    }
}
