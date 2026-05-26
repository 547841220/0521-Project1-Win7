package com.aimoney.fieldledger.win7;

import com.aimoney.fieldledger.win7.Models.FixedExpense;
import com.aimoney.fieldledger.win7.Models.LaborRecord;
import com.aimoney.fieldledger.win7.Models.Manager;
import com.aimoney.fieldledger.win7.Models.Material;
import com.aimoney.fieldledger.win7.Models.Plot;
import com.aimoney.fieldledger.win7.Models.PlotInput;
import com.aimoney.fieldledger.win7.Models.PublicExpense;
import com.aimoney.fieldledger.win7.Models.Shipment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

final class LedgerStore {
    static final String PUBLIC_TYPE_NORMAL = "普通公账";
    static final String PUBLIC_TYPE_AVERAGE = "公账平均";
    static final String[] PUBLIC_EXPENSE_TYPES = { PUBLIC_TYPE_NORMAL, PUBLIC_TYPE_AVERAGE };
    static final String[] PUBLIC_EXPENSE_CATEGORIES = { "日常生活", "电费", "材料", "药", "肥", "油耗", "固定消耗", "其他" };

    final File dataDir;
    final List<Manager> managers = new ArrayList<Manager>();
    final List<Plot> plots = new ArrayList<Plot>();
    final List<Material> materials = new ArrayList<Material>();
    final List<PlotInput> plotInputs = new ArrayList<PlotInput>();
    final List<LaborRecord> laborRecords = new ArrayList<LaborRecord>();
    final List<Shipment> shipments = new ArrayList<Shipment>();
    final List<PublicExpense> publicExpenses = new ArrayList<PublicExpense>();
    final List<FixedExpense> fixedExpenses = new ArrayList<FixedExpense>();

    long manualTotalCents;
    String expenseCheckNote = "";

    LedgerStore() {
        this.dataDir = defaultDataDir();
    }

    void load() throws IOException {
        managers.clear();
        plots.clear();
        materials.clear();
        plotInputs.clear();
        laborRecords.clear();
        shipments.clear();
        publicExpenses.clear();
        fixedExpenses.clear();

        managers.addAll(TextStore.readRows(file("managers.tsv"), new TextStore.RowReader<Manager>() {
            public Manager read(String[] values) {
                return new Manager(values);
            }
        }));
        plots.addAll(TextStore.readRows(file("plots.tsv"), new TextStore.RowReader<Plot>() {
            public Plot read(String[] values) {
                return new Plot(values);
            }
        }));
        materials.addAll(TextStore.readRows(file("materials.tsv"), new TextStore.RowReader<Material>() {
            public Material read(String[] values) {
                return new Material(values);
            }
        }));
        plotInputs.addAll(TextStore.readRows(file("plot-inputs.tsv"), new TextStore.RowReader<PlotInput>() {
            public PlotInput read(String[] values) {
                return new PlotInput(values);
            }
        }));
        laborRecords.addAll(TextStore.readRows(file("labor-records.tsv"), new TextStore.RowReader<LaborRecord>() {
            public LaborRecord read(String[] values) {
                return new LaborRecord(values);
            }
        }));
        shipments.addAll(TextStore.readRows(file("shipments.tsv"), new TextStore.RowReader<Shipment>() {
            public Shipment read(String[] values) {
                return new Shipment(values);
            }
        }));
        publicExpenses.addAll(TextStore.readRows(file("public-expenses.tsv"), new TextStore.RowReader<PublicExpense>() {
            public PublicExpense read(String[] values) {
                return new PublicExpense(values);
            }
        }));
        fixedExpenses.addAll(TextStore.readRows(file("fixed-expenses.tsv"), new TextStore.RowReader<FixedExpense>() {
            public FixedExpense read(String[] values) {
                return new FixedExpense(values);
            }
        }));
        loadExpenseCheck();
        seedIfEmpty();
        saveAll();
    }

    void saveAll() throws IOException {
        TextStore.writeRows(file("managers.tsv"), managers, new TextStore.RowWriter<Manager>() {
            public String[] write(Manager value) {
                return value.values();
            }
        });
        TextStore.writeRows(file("plots.tsv"), plots, new TextStore.RowWriter<Plot>() {
            public String[] write(Plot value) {
                return value.values();
            }
        });
        TextStore.writeRows(file("materials.tsv"), materials, new TextStore.RowWriter<Material>() {
            public String[] write(Material value) {
                return value.values();
            }
        });
        TextStore.writeRows(file("plot-inputs.tsv"), plotInputs, new TextStore.RowWriter<PlotInput>() {
            public String[] write(PlotInput value) {
                return value.values();
            }
        });
        TextStore.writeRows(file("labor-records.tsv"), laborRecords, new TextStore.RowWriter<LaborRecord>() {
            public String[] write(LaborRecord value) {
                return value.values();
            }
        });
        TextStore.writeRows(file("shipments.tsv"), shipments, new TextStore.RowWriter<Shipment>() {
            public String[] write(Shipment value) {
                return value.values();
            }
        });
        TextStore.writeRows(file("public-expenses.tsv"), publicExpenses, new TextStore.RowWriter<PublicExpense>() {
            public String[] write(PublicExpense value) {
                return value.values();
            }
        });
        TextStore.writeRows(file("fixed-expenses.tsv"), fixedExpenses, new TextStore.RowWriter<FixedExpense>() {
            public String[] write(FixedExpense value) {
                return value.values();
            }
        });
        saveExpenseCheck();
    }

    Summary summary() {
        Summary summary = new Summary();
        summary.plotCount = plots.size();
        summary.managerCount = managers.size();
        for (Shipment row : shipments) {
            summary.totalIncomeCents += row.totalCents();
        }
        for (PlotInput row : plotInputs) {
            summary.plotInputCents += row.totalCents();
        }
        for (LaborRecord row : laborRecords) {
            summary.laborCents += row.totalCents();
        }
        for (PublicExpense row : publicExpenses) {
            summary.publicExpenseCents += row.totalCents();
            if (PUBLIC_TYPE_AVERAGE.equals(row.accountType)) {
                summary.publicAverageExpenseCents += row.totalCents();
            } else {
                summary.publicNormalExpenseCents += row.totalCents();
            }
        }
        for (FixedExpense row : fixedExpenses) {
            summary.fixedExpenseCents += row.amountCents;
        }
        summary.directCostCents = summary.plotInputCents + summary.laborCents;
        summary.operatingExpenseCents = summary.directCostCents + summary.publicExpenseCents + summary.fixedExpenseCents;
        summary.profitCents = summary.totalIncomeCents - summary.directCostCents;
        summary.manualTotalCents = manualTotalCents;
        summary.differenceCents = manualTotalCents == 0L ? 0L : manualTotalCents - summary.operatingExpenseCents;
        return summary;
    }

    List<PlotProfit> plotProfits() {
        List<PlotProfit> rows = new ArrayList<PlotProfit>();
        for (Plot plot : plots) {
            PlotProfit profit = new PlotProfit();
            profit.plotId = plot.id;
            profit.plotCode = plot.code;
            profit.managerName = managerName(plot.managerId);
            for (Shipment shipment : shipments) {
                if (plot.id.equals(shipment.plotId)) {
                    profit.incomeCents += shipment.totalCents();
                }
            }
            for (PlotInput input : plotInputs) {
                if (plot.id.equals(input.plotId)) {
                    profit.plotInputCents += input.totalCents();
                }
            }
            for (LaborRecord labor : laborRecords) {
                if (plot.id.equals(labor.plotId)) {
                    profit.laborCents += labor.totalCents();
                }
            }
            profit.directCostCents = profit.plotInputCents + profit.laborCents;
            profit.profitCents = profit.incomeCents - profit.directCostCents;
            rows.add(profit);
        }
        return rows;
    }

    File exportCsv() throws IOException {
        File dir = new File(dataDir, "exports" + File.separator + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()));
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("无法创建导出目录: " + dir.getAbsolutePath());
        }

        CsvWriter.write(new File(dir, "01-地块利润汇总.csv"), new String[] { "地块", "管理人", "出货收入", "地块投入", "工资用工", "直接成本", "利润" }, plotProfitRows());
        CsvWriter.write(new File(dir, "02-地块投入明细.csv"), new String[] { "日期", "地块", "管理人", "类别", "名称", "数量", "单位", "单价", "金额", "备注" }, plotInputRows());
        CsvWriter.write(new File(dir, "03-工资用工明细.csv"), new String[] { "日期", "地块", "管理人", "项目", "男工数", "男工单价", "男工金额", "女工数", "女工单价", "女工金额", "车费", "合计", "备注" }, laborRows());
        CsvWriter.write(new File(dir, "04-出货记录.csv"), new String[] { "日期", "地块", "管理人", "车次", "毛重公斤", "毛重斤", "筐数", "单筐皮重", "总皮重", "扣除名目", "扣除重量", "净重斤", "单价", "总价", "备注" }, shipmentRows());
        CsvWriter.write(new File(dir, "05-公账支出.csv"), new String[] { "账目类型", "日期", "类别", "名称", "数量", "单位", "单价", "金额", "备注" }, publicExpenseRows());
        CsvWriter.write(new File(dir, "06-固定账.csv"), new String[] { "日期", "类别", "名称", "金额", "使用月数", "备注" }, fixedExpenseRows());
        CsvWriter.write(new File(dir, "07-总账核对.csv"), new String[] { "项目", "金额" }, checkRows());
        CsvWriter.write(new File(dir, "08-公账分类汇总.csv"), new String[] { "账目类型", "类别", "金额" }, publicExpenseSummaryRows());
        return dir;
    }

    String managerName(String id) {
        Manager manager = findManager(id);
        return manager == null ? "" : manager.name;
    }

    String plotCode(String id) {
        Plot plot = findPlot(id);
        return plot == null ? "" : plot.code;
    }

    String plotManagerName(String plotId) {
        Plot plot = findPlot(plotId);
        return plot == null ? "" : managerName(plot.managerId);
    }

    Manager findManager(String id) {
        for (Manager row : managers) {
            if (row.id.equals(id)) {
                return row;
            }
        }
        return null;
    }

    Plot findPlot(String id) {
        for (Plot row : plots) {
            if (row.id.equals(id)) {
                return row;
            }
        }
        return null;
    }

    Material findMaterial(String id) {
        for (Material row : materials) {
            if (row.id.equals(id)) {
                return row;
            }
        }
        return null;
    }

    private List<String[]> plotProfitRows() {
        List<String[]> rows = new ArrayList<String[]>();
        for (PlotProfit row : plotProfits()) {
            rows.add(new String[] {
                row.plotCode,
                row.managerName,
                Money.centsToYuan(row.incomeCents),
                Money.centsToYuan(row.plotInputCents),
                Money.centsToYuan(row.laborCents),
                Money.centsToYuan(row.directCostCents),
                Money.centsToYuan(row.profitCents)
            });
        }
        return rows;
    }

    private List<String[]> plotInputRows() {
        List<String[]> rows = new ArrayList<String[]>();
        for (PlotInput row : plotInputs) {
            rows.add(new String[] { row.date, plotCode(row.plotId), plotManagerName(row.plotId), row.category, row.name, Money.number(row.quantity), row.unit, Money.centsToYuan(row.unitPriceCents), Money.centsToYuan(row.totalCents()), row.note });
        }
        return rows;
    }

    private List<String[]> laborRows() {
        List<String[]> rows = new ArrayList<String[]>();
        for (LaborRecord row : laborRecords) {
            rows.add(new String[] { row.date, plotCode(row.plotId), plotManagerName(row.plotId), row.projectName, Money.number(row.maleCount), Money.centsToYuan(row.malePriceCents), Money.centsToYuan(row.maleAmountCents()), Money.number(row.femaleCount), Money.centsToYuan(row.femalePriceCents), Money.centsToYuan(row.femaleAmountCents()), Money.centsToYuan(row.vehicleAmountCents), Money.centsToYuan(row.totalCents()), row.note });
        }
        return rows;
    }

    private List<String[]> shipmentRows() {
        List<String[]> rows = new ArrayList<String[]>();
        for (Shipment row : shipments) {
            rows.add(new String[] { row.date, plotCode(row.plotId), plotManagerName(row.plotId), String.valueOf(row.batchNo), Money.number(row.grossWeightKg), Money.number(row.grossWeightJin()), Money.number(row.basketCount), Money.number(row.basketWeight), Money.number(row.tareWeight()), row.deductionName, Money.number(row.deductionWeight), Money.number(row.netWeightJin()), Money.centsToYuan(row.unitPriceCents), Money.centsToYuan(row.totalCents()), row.note });
        }
        return rows;
    }

    private List<String[]> publicExpenseRows() {
        List<String[]> rows = new ArrayList<String[]>();
        for (PublicExpense row : publicExpenses) {
            rows.add(new String[] { publicType(row.accountType), row.date, row.category, row.name, Money.number(row.quantity), row.unit, Money.centsToYuan(row.unitPriceCents), Money.centsToYuan(row.totalCents()), row.note });
        }
        return rows;
    }

    List<String[]> publicExpenseSummaryRows() {
        List<String[]> rows = new ArrayList<String[]>();
        for (String type : PUBLIC_EXPENSE_TYPES) {
            for (String category : PUBLIC_EXPENSE_CATEGORIES) {
                long total = publicExpenseTotal(type, category);
                if (total != 0L) {
                    rows.add(new String[] { type, category, Money.centsToYuan(total) });
                }
            }
        }
        long otherNormal = publicExpenseUnlistedTotal(PUBLIC_TYPE_NORMAL);
        if (otherNormal != 0L) {
            rows.add(new String[] { PUBLIC_TYPE_NORMAL, "未分类", Money.centsToYuan(otherNormal) });
        }
        long otherAverage = publicExpenseUnlistedTotal(PUBLIC_TYPE_AVERAGE);
        if (otherAverage != 0L) {
            rows.add(new String[] { PUBLIC_TYPE_AVERAGE, "未分类", Money.centsToYuan(otherAverage) });
        }
        return rows;
    }

    private List<String[]> fixedExpenseRows() {
        List<String[]> rows = new ArrayList<String[]>();
        for (FixedExpense row : fixedExpenses) {
            rows.add(new String[] { row.date, row.category, row.name, Money.centsToYuan(row.amountCents), row.usefulLifeMonths == 0 ? "" : String.valueOf(row.usefulLifeMonths), row.note });
        }
        return rows;
    }

    private List<String[]> checkRows() {
        Summary summary = summary();
        List<String[]> rows = new ArrayList<String[]>();
        rows.add(new String[] { "地块投入", Money.centsToYuan(summary.plotInputCents) });
        rows.add(new String[] { "工资用工", Money.centsToYuan(summary.laborCents) });
        rows.add(new String[] { "小组账合计/直接成本", Money.centsToYuan(summary.directCostCents) });
        rows.add(new String[] { "普通公账", Money.centsToYuan(summary.publicNormalExpenseCents) });
        rows.add(new String[] { "公账平均", Money.centsToYuan(summary.publicAverageExpenseCents) });
        rows.add(new String[] { "公账支出", Money.centsToYuan(summary.publicExpenseCents) });
        rows.add(new String[] { "固定账", Money.centsToYuan(summary.fixedExpenseCents) });
        rows.add(new String[] { "系统总开销", Money.centsToYuan(summary.operatingExpenseCents) });
        rows.add(new String[] { "手工总开销", manualTotalCents == 0L ? "" : Money.centsToYuan(manualTotalCents) });
        rows.add(new String[] { "差额", manualTotalCents == 0L ? "" : Money.centsToYuan(summary.differenceCents) });
        rows.add(new String[] { "备注", expenseCheckNote });
        return rows;
    }

    long publicExpenseTotal(String accountType, String category) {
        long total = 0L;
        for (PublicExpense row : publicExpenses) {
            if (publicType(row.accountType).equals(accountType) && category.equals(row.category)) {
                total += row.totalCents();
            }
        }
        return total;
    }

    private long publicExpenseUnlistedTotal(String accountType) {
        long total = 0L;
        for (PublicExpense row : publicExpenses) {
            if (publicType(row.accountType).equals(accountType) && !isKnownPublicCategory(row.category)) {
                total += row.totalCents();
            }
        }
        return total;
    }

    static String publicType(String value) {
        if (PUBLIC_TYPE_AVERAGE.equals(value)) {
            return PUBLIC_TYPE_AVERAGE;
        }
        return PUBLIC_TYPE_NORMAL;
    }

    private static boolean isKnownPublicCategory(String category) {
        for (String known : PUBLIC_EXPENSE_CATEGORIES) {
            if (known.equals(category)) {
                return true;
            }
        }
        return false;
    }

    private void seedIfEmpty() {
        if (managers.isEmpty()) {
            managers.add(new Manager("寇宝权", "宝权", "", ""));
            managers.add(new Manager("郭宏宝", "红宝", "", ""));
            managers.add(new Manager("曹润生", "老曹", "", ""));
        }
        if (plots.isEmpty()) {
            plots.add(new Plot("1号", "", managers.get(0).id, 1D, "", ""));
            plots.add(new Plot("2号", "", managers.get(1).id, 1D, "", ""));
            plots.add(new Plot("3号", "", managers.get(2).id, 1D, "", ""));
            plots.add(new Plot("4号", "", managers.get(0).id, 1D, "", ""));
        }
    }

    private void loadExpenseCheck() throws IOException {
        File file = file("expense-check.properties");
        if (!file.exists()) {
            return;
        }
        Properties properties = new Properties();
        java.io.FileInputStream in = new java.io.FileInputStream(file);
        try {
            properties.load(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
        } finally {
            in.close();
        }
        manualTotalCents = Long.parseLong(properties.getProperty("manualTotalCents", "0"));
        expenseCheckNote = properties.getProperty("note", "");
    }

    private void saveExpenseCheck() throws IOException {
        File file = file("expense-check.properties");
        TextStore.ensureParent(file);
        Properties properties = new Properties();
        properties.setProperty("manualTotalCents", String.valueOf(manualTotalCents));
        properties.setProperty("note", expenseCheckNote == null ? "" : expenseCheckNote);
        java.io.FileOutputStream out = new java.io.FileOutputStream(file);
        try {
            properties.store(new java.io.OutputStreamWriter(out, java.nio.charset.StandardCharsets.UTF_8), "Field ledger expense check");
        } finally {
            out.close();
        }
    }

    private File file(String name) {
        return new File(dataDir, name);
    }

    private static File defaultDataDir() {
        String appData = System.getenv("APPDATA");
        if (appData != null && !appData.trim().isEmpty()) {
            return new File(appData, "AiMoneyFieldLedgerWin7");
        }
        return new File(System.getProperty("user.home"), ".aimoney-field-ledger-win7");
    }

    static final class Summary {
        int plotCount;
        int managerCount;
        long totalIncomeCents;
        long plotInputCents;
        long laborCents;
        long directCostCents;
        long publicExpenseCents;
        long publicNormalExpenseCents;
        long publicAverageExpenseCents;
        long fixedExpenseCents;
        long operatingExpenseCents;
        long profitCents;
        long manualTotalCents;
        long differenceCents;
    }

    static final class PlotProfit {
        String plotId;
        String plotCode;
        String managerName;
        long incomeCents;
        long plotInputCents;
        long laborCents;
        long directCostCents;
        long profitCents;
    }
}
