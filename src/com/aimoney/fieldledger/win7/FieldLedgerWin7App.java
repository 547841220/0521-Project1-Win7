package com.aimoney.fieldledger.win7;

import com.aimoney.fieldledger.win7.LedgerStore.PlotProfit;
import com.aimoney.fieldledger.win7.LedgerStore.Summary;
import com.aimoney.fieldledger.win7.Models.FixedExpense;
import com.aimoney.fieldledger.win7.Models.LaborRecord;
import com.aimoney.fieldledger.win7.Models.Manager;
import com.aimoney.fieldledger.win7.Models.Material;
import com.aimoney.fieldledger.win7.Models.Plot;
import com.aimoney.fieldledger.win7.Models.PlotInput;
import com.aimoney.fieldledger.win7.Models.PublicExpense;
import com.aimoney.fieldledger.win7.Models.Shipment;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class FieldLedgerWin7App {
    private static final Color DARK = new Color(23, 32, 27);
    private static final Color DARK_ACTIVE = new Color(38, 54, 46);
    private static final Color PAPER = new Color(244, 241, 234);
    private static final Color PANEL = new Color(255, 253, 248);
    private static final Color GOLD = new Color(229, 180, 90);

    private final LedgerStore store = new LedgerStore();
    private final JFrame frame = new JFrame("农业经营记账 - Win7兼容版");
    private final JPanel content = new JPanel(new BorderLayout());
    private final JLabel title = new JLabel("经营概览");
    private final JLabel status = new JLabel("正在准备本地数据");
    private final List<JButton> navButtons = new ArrayList<JButton>();
    private String currentPage = "dashboard";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new FieldLedgerWin7App().start();
            }
        });
    }

    private void start() {
        setLookAndFeel();
        try {
            store.load();
            status.setText("本地账套已就绪");
        } catch (Exception ex) {
            showError("本地数据初始化失败", ex);
        }

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1120, 720));
        frame.setLayout(new BorderLayout());
        frame.add(sidebar(), BorderLayout.WEST);
        frame.add(workspace(), BorderLayout.CENTER);
        openPage("dashboard");
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel sidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(248, 720));
        panel.setBackground(DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 18, 24, 18));

        JPanel brand = new JPanel(new BorderLayout(12, 0));
        brand.setOpaque(false);
        JLabel mark = new JLabel("田", JLabel.CENTER);
        mark.setOpaque(true);
        mark.setBackground(GOLD);
        mark.setForeground(DARK);
        mark.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        mark.setPreferredSize(new Dimension(42, 42));
        JLabel name = new JLabel("<html><b>农业经营记账</b><br><span style='font-size:10px'>Win7兼容版</span></html>");
        name.setForeground(Color.WHITE);
        brand.add(mark, BorderLayout.WEST);
        brand.add(name, BorderLayout.CENTER);

        JPanel nav = new JPanel(new GridLayout(0, 1, 0, 4));
        nav.setOpaque(false);
        addNav(nav, "dashboard", "首页概览");
        addNav(nav, "plots", "地块管理");
        addNav(nav, "managers", "管理人管理");
        addNav(nav, "materials", "物料价格");
        addNav(nav, "plotInputs", "地块投入");
        addNav(nav, "labor", "工资用工");
        addNav(nav, "shipments", "出货记录");
        addNav(nav, "publicExpenses", "公账支出");
        addNav(nav, "fixedExpenses", "固定账");
        addNav(nav, "profit", "利润核对");
        addNav(nav, "export", "Excel导出");

        panel.add(brand, BorderLayout.NORTH);
        panel.add(nav, BorderLayout.CENTER);
        return panel;
    }

    private JPanel workspace() {
        JPanel panel = new JPanel(new BorderLayout(0, 22));
        panel.setBackground(PAPER);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setOpaque(false);
        JLabel season = new JLabel("2026年第一期");
        season.setForeground(new Color(104, 115, 109));
        season.setFont(season.getFont().deriveFont(Font.BOLD, 12F));
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 30));
        title.setForeground(new Color(30, 40, 34));
        JPanel titleBox = new JPanel(new BorderLayout());
        titleBox.setOpaque(false);
        titleBox.add(season, BorderLayout.NORTH);
        titleBox.add(title, BorderLayout.CENTER);

        status.setOpaque(true);
        status.setBackground(new Color(255, 250, 240));
        status.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(215, 208, 194)), BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        topbar.add(titleBox, BorderLayout.WEST);
        topbar.add(status, BorderLayout.EAST);

        panel.add(topbar, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private void addNav(JPanel nav, final String key, String label) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        button.setForeground(new Color(220, 227, 220));
        button.setBackground(DARK);
        button.addActionListener(e -> openPage(key));
        navButtons.add(button);
        nav.add(button);
    }

    private void openPage(String key) {
        currentPage = key;
        for (JButton button : navButtons) {
            button.setBackground(DARK);
            button.setForeground(new Color(220, 227, 220));
        }
        int index = pageIndex(key);
        if (index >= 0 && index < navButtons.size()) {
            navButtons.get(index).setBackground(DARK_ACTIVE);
            navButtons.get(index).setForeground(Color.WHITE);
        }

        content.removeAll();
        if ("dashboard".equals(key)) {
            title.setText("经营概览");
            content.add(dashboardPage(), BorderLayout.CENTER);
        } else if ("plots".equals(key)) {
            title.setText("地块管理");
            content.add(plotsPage(), BorderLayout.CENTER);
        } else if ("managers".equals(key)) {
            title.setText("管理人管理");
            content.add(managersPage(), BorderLayout.CENTER);
        } else if ("materials".equals(key)) {
            title.setText("物料价格");
            content.add(materialsPage(), BorderLayout.CENTER);
        } else if ("plotInputs".equals(key)) {
            title.setText("地块投入");
            content.add(plotInputsPage(), BorderLayout.CENTER);
        } else if ("labor".equals(key)) {
            title.setText("工资用工");
            content.add(laborPage(), BorderLayout.CENTER);
        } else if ("shipments".equals(key)) {
            title.setText("出货记录");
            content.add(shipmentsPage(), BorderLayout.CENTER);
        } else if ("publicExpenses".equals(key)) {
            title.setText("公账支出");
            content.add(publicExpensesPage(), BorderLayout.CENTER);
        } else if ("fixedExpenses".equals(key)) {
            title.setText("固定账");
            content.add(fixedExpensesPage(), BorderLayout.CENTER);
        } else if ("profit".equals(key)) {
            title.setText("利润核对");
            content.add(profitPage(), BorderLayout.CENTER);
        } else {
            title.setText("Excel导出");
            content.add(exportPage(), BorderLayout.CENTER);
        }
        content.revalidate();
        content.repaint();
    }

    private JPanel dashboardPage() {
        Summary summary = store.summary();
        JPanel page = pagePanel();
        JPanel grid = new JPanel(new GridLayout(2, 4, 14, 14));
        grid.setOpaque(false);
        grid.add(metric("出货收入", Money.centsToYuan(summary.totalIncomeCents)));
        grid.add(metric("直接成本", Money.centsToYuan(summary.directCostCents)));
        grid.add(metric("地块利润", Money.centsToYuan(summary.profitCents)));
        grid.add(metric("经营总开销", Money.centsToYuan(summary.operatingExpenseCents)));
        grid.add(metric("地块数量", String.valueOf(summary.plotCount)));
        grid.add(metric("管理人数", String.valueOf(summary.managerCount)));
        grid.add(metric("公账支出", Money.centsToYuan(summary.publicExpenseCents)));
        grid.add(metric("固定账", Money.centsToYuan(summary.fixedExpenseCents)));
        page.add(grid, BorderLayout.NORTH);

        JTable table = table(new String[] { "地块", "管理人", "出货收入", "直接成本", "利润" }, profitTableRows(false));
        page.add(wrapTable("地块利润速览", table), BorderLayout.CENTER);
        return page;
    }

    private JPanel managersPage() {
        final JTextField name = field();
        final JTextField alias = field();
        final JTextField phone = field();
        final JTextField note = field();
        final JTable table = table(new String[] { "姓名", "简称", "电话", "备注" }, managerRows());
        final String[] selectedId = new String[] { "" };

        JPanel form = formPanel();
        addField(form, 0, "姓名", name);
        addField(form, 1, "简称", alias);
        addField(form, 2, "电话", phone);
        addField(form, 3, "备注", note);
        addActions(form, 4, new Runnable() {
            public void run() {
                if (name.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("姓名不能为空");
                }
                Manager row = selectedId[0].isEmpty() ? new Manager(name.getText(), alias.getText(), phone.getText(), note.getText()) : store.findManager(selectedId[0]);
                row.name = name.getText();
                row.alias = alias.getText();
                row.phone = phone.getText();
                row.note = note.getText();
                if (selectedId[0].isEmpty()) {
                    store.managers.add(row);
                }
            }
        }, new Runnable() {
            public void run() {
                store.managers.remove(store.findManager(selectedId[0]));
            }
        }, selectedId);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && row >= 0) {
                Manager manager = store.managers.get(row);
                selectedId[0] = manager.id;
                name.setText(manager.name);
                alias.setText(manager.alias);
                phone.setText(manager.phone);
                note.setText(manager.note);
            }
        });
        return crudPage(form, table);
    }

    private JPanel plotsPage() {
        final JTextField code = field();
        final JTextField name = field();
        final JComboBox<Item> manager = managerCombo();
        final JTextField area = field("1");
        final JTextField crop = field();
        final JTextField note = field();
        final JTable table = table(new String[] { "地块", "名称", "管理人", "面积", "作物", "备注" }, plotRows());
        final String[] selectedId = new String[] { "" };

        JPanel form = formPanel();
        addField(form, 0, "地块编号", code);
        addField(form, 1, "地块名称", name);
        addField(form, 2, "管理人", manager);
        addField(form, 3, "面积(亩)", area);
        addField(form, 4, "作物", crop);
        addField(form, 5, "备注", note);
        addActions(form, 6, new Runnable() {
            public void run() {
                if (code.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("地块编号不能为空");
                }
                Plot row = selectedId[0].isEmpty() ? new Plot(code.getText(), name.getText(), selectedId(manager), Money.parseDouble(area.getText()), crop.getText(), note.getText()) : store.findPlot(selectedId[0]);
                row.code = code.getText();
                row.name = name.getText();
                row.managerId = selectedId(manager);
                row.area = Money.parseDouble(area.getText());
                row.cropName = crop.getText();
                row.note = note.getText();
                if (selectedId[0].isEmpty()) {
                    store.plots.add(row);
                }
            }
        }, new Runnable() {
            public void run() {
                store.plots.remove(store.findPlot(selectedId[0]));
            }
        }, selectedId);
        table.getSelectionModel().addListSelectionListener(e -> {
            int rowIndex = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && rowIndex >= 0) {
                Plot row = store.plots.get(rowIndex);
                selectedId[0] = row.id;
                code.setText(row.code);
                name.setText(row.name);
                selectCombo(manager, row.managerId);
                area.setText(String.valueOf(row.area));
                crop.setText(row.cropName);
                note.setText(row.note);
            }
        });
        return crudPage(form, table);
    }

    private JPanel materialsPage() {
        final JTextField name = field();
        final JTextField category = field();
        final JTextField unit = field();
        final JTextField price = field("0");
        final JCheckBox enabled = new JCheckBox("启用", true);
        final JTextField note = field();
        final JTable table = table(new String[] { "名称", "类别", "单位", "参考单价", "启用", "备注" }, materialRows());
        final String[] selectedId = new String[] { "" };

        JPanel form = formPanel();
        addField(form, 0, "名称", name);
        addField(form, 1, "类别", category);
        addField(form, 2, "单位", unit);
        addField(form, 3, "参考单价", price);
        addField(form, 4, "状态", enabled);
        addField(form, 5, "备注", note);
        addActions(form, 6, new Runnable() {
            public void run() {
                if (name.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("物料名称不能为空");
                }
                Material row = selectedId[0].isEmpty() ? new Material(name.getText(), category.getText(), unit.getText(), Money.yuanToCents(price.getText()), enabled.isSelected(), note.getText()) : store.findMaterial(selectedId[0]);
                row.name = name.getText();
                row.category = category.getText();
                row.unit = unit.getText();
                row.referencePriceCents = Money.yuanToCents(price.getText());
                row.enabled = enabled.isSelected();
                row.note = note.getText();
                if (selectedId[0].isEmpty()) {
                    store.materials.add(row);
                }
            }
        }, new Runnable() {
            public void run() {
                store.materials.remove(store.findMaterial(selectedId[0]));
            }
        }, selectedId);
        table.getSelectionModel().addListSelectionListener(e -> {
            int rowIndex = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && rowIndex >= 0) {
                Material row = store.materials.get(rowIndex);
                selectedId[0] = row.id;
                name.setText(row.name);
                category.setText(row.category);
                unit.setText(row.unit);
                price.setText(Money.centsInput(row.referencePriceCents));
                enabled.setSelected(row.enabled);
                note.setText(row.note);
            }
        });
        return crudPage(form, table);
    }

    private JPanel plotInputsPage() {
        final JTextField date = field(today());
        final JComboBox<Item> plot = plotCombo();
        final JTextField category = field("农药");
        final JComboBox<Item> material = materialCombo();
        final JTextField name = field();
        final JTextField quantity = field("1");
        final JTextField unit = field();
        final JTextField price = field("0");
        final JTextField note = field();
        final JTable table = table(new String[] { "日期", "地块", "管理人", "类别", "名称", "数量", "单位", "单价", "金额", "备注" }, plotInputRows());
        final String[] selectedId = new String[] { "" };

        material.addActionListener(e -> {
            Material selected = store.findMaterial(selectedId(material));
            if (selected != null) {
                name.setText(selected.name);
                unit.setText(selected.unit);
                price.setText(Money.centsInput(selected.referencePriceCents));
                category.setText(selected.category);
            }
        });

        JPanel form = formPanel();
        addField(form, 0, "日期", date);
        addField(form, 1, "地块", plot);
        addField(form, 2, "类别", category);
        addField(form, 3, "物料", material);
        addField(form, 4, "名称", name);
        addField(form, 5, "数量", quantity);
        addField(form, 6, "单位", unit);
        addField(form, 7, "单价", price);
        addField(form, 8, "备注", note);
        addActions(form, 9, new Runnable() {
            public void run() {
                PlotInput row = selectedId[0].isEmpty() ? new PlotInput(date.getText(), selectedId(plot), category.getText(), selectedId(material), name.getText(), Money.parseDouble(quantity.getText()), unit.getText(), Money.yuanToCents(price.getText()), note.getText()) : findPlotInput(selectedId[0]);
                row.date = date.getText();
                row.plotId = selectedId(plot);
                row.category = category.getText();
                row.materialId = selectedId(material);
                row.name = name.getText();
                row.quantity = Money.parseDouble(quantity.getText());
                row.unit = unit.getText();
                row.unitPriceCents = Money.yuanToCents(price.getText());
                row.note = note.getText();
                if (selectedId[0].isEmpty()) {
                    store.plotInputs.add(row);
                }
            }
        }, new Runnable() {
            public void run() {
                store.plotInputs.remove(findPlotInput(selectedId[0]));
            }
        }, selectedId);
        table.getSelectionModel().addListSelectionListener(e -> {
            int rowIndex = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && rowIndex >= 0) {
                PlotInput row = store.plotInputs.get(rowIndex);
                selectedId[0] = row.id;
                date.setText(row.date);
                selectCombo(plot, row.plotId);
                category.setText(row.category);
                selectCombo(material, row.materialId);
                name.setText(row.name);
                quantity.setText(String.valueOf(row.quantity));
                unit.setText(row.unit);
                price.setText(Money.centsInput(row.unitPriceCents));
                note.setText(row.note);
            }
        });
        return crudPage(form, table);
    }

    private JPanel laborPage() {
        final JTextField date = field(today());
        final JComboBox<Item> plot = plotCombo();
        final JTextField project = field();
        final JTextField maleCount = field("0");
        final JTextField malePrice = field("0");
        final JTextField femaleCount = field("0");
        final JTextField femalePrice = field("0");
        final JTextField vehicle = field("0");
        final JTextField note = field();
        final JTable table = table(new String[] { "日期", "地块", "管理人", "项目", "男工数", "男工单价", "男工金额", "女工数", "女工单价", "女工金额", "车费", "合计", "备注" }, laborRows());
        final String[] selectedId = new String[] { "" };

        JPanel form = formPanel();
        addField(form, 0, "日期", date);
        addField(form, 1, "地块", plot);
        addField(form, 2, "项目", project);
        addField(form, 3, "男工数", maleCount);
        addField(form, 4, "男工单价", malePrice);
        addField(form, 5, "女工数", femaleCount);
        addField(form, 6, "女工单价", femalePrice);
        addField(form, 7, "车费", vehicle);
        addField(form, 8, "备注", note);
        addActions(form, 9, new Runnable() {
            public void run() {
                LaborRecord row = selectedId[0].isEmpty() ? new LaborRecord(date.getText(), selectedId(plot), project.getText(), Money.parseDouble(maleCount.getText()), Money.yuanToCents(malePrice.getText()), Money.parseDouble(femaleCount.getText()), Money.yuanToCents(femalePrice.getText()), Money.yuanToCents(vehicle.getText()), note.getText()) : findLabor(selectedId[0]);
                row.date = date.getText();
                row.plotId = selectedId(plot);
                row.projectName = project.getText();
                row.maleCount = Money.parseDouble(maleCount.getText());
                row.malePriceCents = Money.yuanToCents(malePrice.getText());
                row.femaleCount = Money.parseDouble(femaleCount.getText());
                row.femalePriceCents = Money.yuanToCents(femalePrice.getText());
                row.vehicleAmountCents = Money.yuanToCents(vehicle.getText());
                row.note = note.getText();
                if (selectedId[0].isEmpty()) {
                    store.laborRecords.add(row);
                }
            }
        }, new Runnable() {
            public void run() {
                store.laborRecords.remove(findLabor(selectedId[0]));
            }
        }, selectedId);
        table.getSelectionModel().addListSelectionListener(e -> {
            int rowIndex = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && rowIndex >= 0) {
                LaborRecord row = store.laborRecords.get(rowIndex);
                selectedId[0] = row.id;
                date.setText(row.date);
                selectCombo(plot, row.plotId);
                project.setText(row.projectName);
                maleCount.setText(String.valueOf(row.maleCount));
                malePrice.setText(Money.centsInput(row.malePriceCents));
                femaleCount.setText(String.valueOf(row.femaleCount));
                femalePrice.setText(Money.centsInput(row.femalePriceCents));
                vehicle.setText(Money.centsInput(row.vehicleAmountCents));
                note.setText(row.note);
            }
        });
        return crudPage(form, table);
    }

    private JPanel shipmentsPage() {
        final JTextField date = field(today());
        final JComboBox<Item> plot = plotCombo();
        final JTextField batch = field("0");
        final JTextField grossKg = field("0");
        final JTextField basketCount = field("0");
        final JTextField basketWeight = field("0");
        final JTextField deductionName = field();
        final JTextField deductionWeight = field("0");
        final JTextField price = field("0");
        final JTextField note = field();
        final JTable table = table(new String[] { "日期", "地块", "管理人", "车次", "毛重公斤", "毛重斤", "筐数", "单筐皮重", "总皮重", "扣除", "扣重", "净重斤", "单价", "总价", "备注" }, shipmentRows());
        final String[] selectedId = new String[] { "" };

        JPanel form = formPanel();
        addField(form, 0, "日期", date);
        addField(form, 1, "地块", plot);
        addField(form, 2, "车次", batch);
        addField(form, 3, "毛重公斤", grossKg);
        addField(form, 4, "筐数", basketCount);
        addField(form, 5, "单筐皮重", basketWeight);
        addField(form, 6, "扣除名目", deductionName);
        addField(form, 7, "扣除重量", deductionWeight);
        addField(form, 8, "单价", price);
        addField(form, 9, "备注", note);
        addActions(form, 10, new Runnable() {
            public void run() {
                Shipment row = selectedId[0].isEmpty() ? new Shipment(date.getText(), selectedId(plot), Money.parseInt(batch.getText()), Money.parseDouble(grossKg.getText()), Money.parseDouble(basketCount.getText()), Money.parseDouble(basketWeight.getText()), deductionName.getText(), Money.parseDouble(deductionWeight.getText()), Money.yuanToCents(price.getText()), note.getText()) : findShipment(selectedId[0]);
                row.date = date.getText();
                row.plotId = selectedId(plot);
                row.batchNo = Money.parseInt(batch.getText());
                row.grossWeightKg = Money.parseDouble(grossKg.getText());
                row.basketCount = Money.parseDouble(basketCount.getText());
                row.basketWeight = Money.parseDouble(basketWeight.getText());
                row.deductionName = deductionName.getText();
                row.deductionWeight = Money.parseDouble(deductionWeight.getText());
                row.unitPriceCents = Money.yuanToCents(price.getText());
                row.note = note.getText();
                if (selectedId[0].isEmpty()) {
                    store.shipments.add(row);
                }
            }
        }, new Runnable() {
            public void run() {
                store.shipments.remove(findShipment(selectedId[0]));
            }
        }, selectedId);
        table.getSelectionModel().addListSelectionListener(e -> {
            int rowIndex = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && rowIndex >= 0) {
                Shipment row = store.shipments.get(rowIndex);
                selectedId[0] = row.id;
                date.setText(row.date);
                selectCombo(plot, row.plotId);
                batch.setText(String.valueOf(row.batchNo));
                grossKg.setText(String.valueOf(row.grossWeightKg));
                basketCount.setText(String.valueOf(row.basketCount));
                basketWeight.setText(String.valueOf(row.basketWeight));
                deductionName.setText(row.deductionName);
                deductionWeight.setText(String.valueOf(row.deductionWeight));
                price.setText(Money.centsInput(row.unitPriceCents));
                note.setText(row.note);
            }
        });
        return crudPage(form, table);
    }

    private JPanel publicExpensesPage() {
        final JTextField date = field(today());
        final JTextField category = field();
        final JTextField name = field();
        final JTextField quantity = field("1");
        final JTextField unit = field();
        final JTextField price = field("0");
        final JTextField note = field();
        final JTable table = table(new String[] { "日期", "类别", "名称", "数量", "单位", "单价", "金额", "备注" }, publicExpenseRows());
        final String[] selectedId = new String[] { "" };

        JPanel form = formPanel();
        addField(form, 0, "日期", date);
        addField(form, 1, "类别", category);
        addField(form, 2, "名称", name);
        addField(form, 3, "数量", quantity);
        addField(form, 4, "单位", unit);
        addField(form, 5, "单价", price);
        addField(form, 6, "备注", note);
        addActions(form, 7, new Runnable() {
            public void run() {
                PublicExpense row = selectedId[0].isEmpty() ? new PublicExpense(date.getText(), category.getText(), name.getText(), Money.parseDouble(quantity.getText()), unit.getText(), Money.yuanToCents(price.getText()), note.getText()) : findPublicExpense(selectedId[0]);
                row.date = date.getText();
                row.category = category.getText();
                row.name = name.getText();
                row.quantity = Money.parseDouble(quantity.getText());
                row.unit = unit.getText();
                row.unitPriceCents = Money.yuanToCents(price.getText());
                row.note = note.getText();
                if (selectedId[0].isEmpty()) {
                    store.publicExpenses.add(row);
                }
            }
        }, new Runnable() {
            public void run() {
                store.publicExpenses.remove(findPublicExpense(selectedId[0]));
            }
        }, selectedId);
        table.getSelectionModel().addListSelectionListener(e -> {
            int rowIndex = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && rowIndex >= 0) {
                PublicExpense row = store.publicExpenses.get(rowIndex);
                selectedId[0] = row.id;
                date.setText(row.date);
                category.setText(row.category);
                name.setText(row.name);
                quantity.setText(String.valueOf(row.quantity));
                unit.setText(row.unit);
                price.setText(Money.centsInput(row.unitPriceCents));
                note.setText(row.note);
            }
        });
        return crudPage(form, table);
    }

    private JPanel fixedExpensesPage() {
        final JTextField date = field(today());
        final JTextField category = field();
        final JTextField name = field();
        final JTextField amount = field("0");
        final JTextField life = field("0");
        final JTextField note = field();
        final JTable table = table(new String[] { "日期", "类别", "名称", "金额", "使用月数", "备注" }, fixedExpenseRows());
        final String[] selectedId = new String[] { "" };

        JPanel form = formPanel();
        addField(form, 0, "日期", date);
        addField(form, 1, "类别", category);
        addField(form, 2, "名称", name);
        addField(form, 3, "金额", amount);
        addField(form, 4, "使用月数", life);
        addField(form, 5, "备注", note);
        addActions(form, 6, new Runnable() {
            public void run() {
                FixedExpense row = selectedId[0].isEmpty() ? new FixedExpense(date.getText(), category.getText(), name.getText(), Money.yuanToCents(amount.getText()), Money.parseInt(life.getText()), note.getText()) : findFixedExpense(selectedId[0]);
                row.date = date.getText();
                row.category = category.getText();
                row.name = name.getText();
                row.amountCents = Money.yuanToCents(amount.getText());
                row.usefulLifeMonths = Money.parseInt(life.getText());
                row.note = note.getText();
                if (selectedId[0].isEmpty()) {
                    store.fixedExpenses.add(row);
                }
            }
        }, new Runnable() {
            public void run() {
                store.fixedExpenses.remove(findFixedExpense(selectedId[0]));
            }
        }, selectedId);
        table.getSelectionModel().addListSelectionListener(e -> {
            int rowIndex = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && rowIndex >= 0) {
                FixedExpense row = store.fixedExpenses.get(rowIndex);
                selectedId[0] = row.id;
                date.setText(row.date);
                category.setText(row.category);
                name.setText(row.name);
                amount.setText(Money.centsInput(row.amountCents));
                life.setText(String.valueOf(row.usefulLifeMonths));
                note.setText(row.note);
            }
        });
        return crudPage(form, table);
    }

    private JPanel profitPage() {
        Summary summary = store.summary();
        final JTextField manualTotal = field(summary.manualTotalCents == 0L ? "" : Money.centsInput(summary.manualTotalCents));
        final JTextField note = field(store.expenseCheckNote);

        JPanel page = pagePanel();
        page.add(wrapTable("地块利润核对", table(new String[] { "地块", "管理人", "出货收入", "地块投入", "工资用工", "直接成本", "利润" }, profitTableRows(true))), BorderLayout.CENTER);

        JPanel bottom = panelBox();
        bottom.setLayout(new BorderLayout(12, 12));
        JPanel cards = new JPanel(new GridLayout(1, 4, 12, 12));
        cards.setOpaque(false);
        cards.add(metric("小组账合计/直接成本", Money.centsToYuan(summary.directCostCents)));
        cards.add(metric("公账支出", Money.centsToYuan(summary.publicExpenseCents)));
        cards.add(metric("固定账", Money.centsToYuan(summary.fixedExpenseCents)));
        cards.add(metric("系统总开销", Money.centsToYuan(summary.operatingExpenseCents)));
        bottom.add(cards, BorderLayout.NORTH);

        JPanel check = formPanel();
        addField(check, 0, "手工总开销", manualTotal);
        addField(check, 1, "核对备注", note);
        JButton save = actionButton("保存核对");
        save.addActionListener(e -> {
            try {
                store.manualTotalCents = manualTotal.getText().trim().isEmpty() ? 0L : Money.yuanToCents(manualTotal.getText());
                store.expenseCheckNote = note.getText();
                saveAndRefresh();
            } catch (Exception ex) {
                showError("保存失败", ex);
            }
        });
        check.add(save, gbc(2, 1));
        bottom.add(check, BorderLayout.CENTER);
        bottom.add(new JLabel("当前差额: " + (summary.manualTotalCents == 0L ? "未填写手工总开销" : Money.centsToYuan(summary.differenceCents))), BorderLayout.SOUTH);
        page.add(bottom, BorderLayout.SOUTH);
        return page;
    }

    private JPanel exportPage() {
        JPanel page = pagePanel();
        JPanel box = panelBox();
        box.setLayout(new BorderLayout(16, 16));
        JLabel text = new JLabel("<html><h2>导出 Excel 可打开的 CSV 文件</h2><p>会生成地块利润、地块投入、工资用工、出货记录、公账、固定账、总账核对共 7 个文件。</p><p>数据目录: " + store.dataDir.getAbsolutePath() + "</p></html>");
        JButton export = actionButton("导出到本地数据目录");
        export.addActionListener(e -> {
            try {
                File dir = store.exportCsv();
                JOptionPane.showMessageDialog(frame, "导出完成:\n" + dir.getAbsolutePath());
            } catch (Exception ex) {
                showError("导出失败", ex);
            }
        });
        box.add(text, BorderLayout.CENTER);
        box.add(export, BorderLayout.SOUTH);
        page.add(box, BorderLayout.NORTH);
        return page;
    }

    private JPanel crudPage(JPanel form, JTable table) {
        JPanel page = pagePanel();
        page.add(form, BorderLayout.NORTH);
        page.add(wrapTable("明细列表", table), BorderLayout.CENTER);
        return page;
    }

    private void addActions(JPanel form, int row, final Runnable saveAction, final Runnable deleteAction, final String[] selectedId) {
        JButton save = actionButton("保存");
        JButton clear = secondaryButton("清空");
        JButton delete = secondaryButton("删除选中");
        save.addActionListener(e -> {
            try {
                saveAction.run();
                saveAndRefresh();
            } catch (Exception ex) {
                showError("保存失败", ex);
            }
        });
        clear.addActionListener(e -> openPage(currentPage));
        delete.addActionListener(e -> {
            if (selectedId[0].isEmpty()) {
                JOptionPane.showMessageDialog(frame, "请先选择一条记录");
                return;
            }
            if (JOptionPane.showConfirmDialog(frame, "确认删除选中记录？", "确认", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    deleteAction.run();
                    saveAndRefresh();
                } catch (Exception ex) {
                    showError("删除失败", ex);
                }
            }
        });
        JPanel actions = new JPanel(new GridLayout(1, 3, 8, 0));
        actions.setOpaque(false);
        actions.add(save);
        actions.add(clear);
        actions.add(delete);
        form.add(actions, gbcWide(row, 2));
    }

    private void saveAndRefresh() throws Exception {
        store.saveAll();
        status.setText("已保存 " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
        openPage(currentPage);
    }

    private JPanel pagePanel() {
        JPanel page = new JPanel(new BorderLayout(0, 16));
        page.setOpaque(false);
        return page;
    }

    private JPanel panelBox() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(222, 215, 202)), BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        return panel;
    }

    private JPanel formPanel() {
        JPanel form = panelBox();
        form.setLayout(new GridBagLayout());
        return form;
    }

    private JPanel wrapTable(String heading, JTable table) {
        JPanel panel = panelBox();
        panel.setLayout(new BorderLayout(0, 12));
        JLabel label = new JLabel(heading);
        label.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel metric(String label, String value) {
        JPanel panel = panelBox();
        panel.setLayout(new BorderLayout());
        JLabel name = new JLabel(label);
        name.setForeground(new Color(92, 104, 97));
        JLabel amount = new JLabel(value);
        amount.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        amount.setForeground(new Color(30, 40, 34));
        panel.add(name, BorderLayout.NORTH);
        panel.add(amount, BorderLayout.CENTER);
        return panel;
    }

    private JTextField field() {
        return field("");
    }

    private JTextField field(String value) {
        JTextField field = new JTextField(value);
        field.setPreferredSize(new Dimension(140, 32));
        return field;
    }

    private void addField(JPanel form, int row, String label, Component input) {
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));
        form.add(fieldLabel, gbc(row, 0));
        form.add(input, gbc(row, 1));
    }

    private GridBagConstraints gbc(int row, int column) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = column;
        gbc.gridy = row / 2;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = column == 1 ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
        gbc.weightx = column == 1 ? 1D : 0D;
        if (row % 2 == 1) {
            gbc.gridx += 2;
        }
        return gbc;
    }

    private GridBagConstraints gbcWide(int row, int width) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = (row + 1) / 2;
        gbc.gridwidth = width * 2;
        gbc.insets = new Insets(8, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1D;
        return gbc;
    }

    private JButton actionButton(String label) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(61, 105, 74)), BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        button.setBackground(new Color(71, 125, 87));
        button.setForeground(Color.WHITE);
        return button;
    }

    private JButton secondaryButton(String label) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(215, 208, 194)), BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        button.setBackground(new Color(247, 242, 233));
        button.setForeground(new Color(41, 52, 46));
        return button;
    }

    private JTable table(String[] headers, Object[][] rows) {
        JTable table = new JTable(new DefaultTableModel(rows, headers) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        return table;
    }

    private Object[][] managerRows() {
        Object[][] rows = new Object[store.managers.size()][4];
        for (int i = 0; i < store.managers.size(); i++) {
            Manager row = store.managers.get(i);
            rows[i] = new Object[] { row.name, row.alias, row.phone, row.note };
        }
        return rows;
    }

    private Object[][] plotRows() {
        Object[][] rows = new Object[store.plots.size()][6];
        for (int i = 0; i < store.plots.size(); i++) {
            Plot row = store.plots.get(i);
            rows[i] = new Object[] { row.code, row.name, store.managerName(row.managerId), Money.number(row.area), row.cropName, row.note };
        }
        return rows;
    }

    private Object[][] materialRows() {
        Object[][] rows = new Object[store.materials.size()][6];
        for (int i = 0; i < store.materials.size(); i++) {
            Material row = store.materials.get(i);
            rows[i] = new Object[] { row.name, row.category, row.unit, Money.centsToYuan(row.referencePriceCents), row.enabled ? "是" : "否", row.note };
        }
        return rows;
    }

    private Object[][] plotInputRows() {
        Object[][] rows = new Object[store.plotInputs.size()][10];
        for (int i = 0; i < store.plotInputs.size(); i++) {
            PlotInput row = store.plotInputs.get(i);
            rows[i] = new Object[] { row.date, store.plotCode(row.plotId), store.plotManagerName(row.plotId), row.category, row.name, Money.number(row.quantity), row.unit, Money.centsToYuan(row.unitPriceCents), Money.centsToYuan(row.totalCents()), row.note };
        }
        return rows;
    }

    private Object[][] laborRows() {
        Object[][] rows = new Object[store.laborRecords.size()][13];
        for (int i = 0; i < store.laborRecords.size(); i++) {
            LaborRecord row = store.laborRecords.get(i);
            rows[i] = new Object[] { row.date, store.plotCode(row.plotId), store.plotManagerName(row.plotId), row.projectName, Money.number(row.maleCount), Money.centsToYuan(row.malePriceCents), Money.centsToYuan(row.maleAmountCents()), Money.number(row.femaleCount), Money.centsToYuan(row.femalePriceCents), Money.centsToYuan(row.femaleAmountCents()), Money.centsToYuan(row.vehicleAmountCents), Money.centsToYuan(row.totalCents()), row.note };
        }
        return rows;
    }

    private Object[][] shipmentRows() {
        Object[][] rows = new Object[store.shipments.size()][15];
        for (int i = 0; i < store.shipments.size(); i++) {
            Shipment row = store.shipments.get(i);
            rows[i] = new Object[] { row.date, store.plotCode(row.plotId), store.plotManagerName(row.plotId), row.batchNo, Money.number(row.grossWeightKg), Money.number(row.grossWeightJin()), Money.number(row.basketCount), Money.number(row.basketWeight), Money.number(row.tareWeight()), row.deductionName, Money.number(row.deductionWeight), Money.number(row.netWeightJin()), Money.centsToYuan(row.unitPriceCents), Money.centsToYuan(row.totalCents()), row.note };
        }
        return rows;
    }

    private Object[][] publicExpenseRows() {
        Object[][] rows = new Object[store.publicExpenses.size()][8];
        for (int i = 0; i < store.publicExpenses.size(); i++) {
            PublicExpense row = store.publicExpenses.get(i);
            rows[i] = new Object[] { row.date, row.category, row.name, Money.number(row.quantity), row.unit, Money.centsToYuan(row.unitPriceCents), Money.centsToYuan(row.totalCents()), row.note };
        }
        return rows;
    }

    private Object[][] fixedExpenseRows() {
        Object[][] rows = new Object[store.fixedExpenses.size()][6];
        for (int i = 0; i < store.fixedExpenses.size(); i++) {
            FixedExpense row = store.fixedExpenses.get(i);
            rows[i] = new Object[] { row.date, row.category, row.name, Money.centsToYuan(row.amountCents), row.usefulLifeMonths == 0 ? "" : row.usefulLifeMonths, row.note };
        }
        return rows;
    }

    private Object[][] profitTableRows(boolean full) {
        List<PlotProfit> profits = store.plotProfits();
        Object[][] rows = new Object[profits.size()][full ? 7 : 5];
        for (int i = 0; i < profits.size(); i++) {
            PlotProfit row = profits.get(i);
            if (full) {
                rows[i] = new Object[] { row.plotCode, row.managerName, Money.centsToYuan(row.incomeCents), Money.centsToYuan(row.plotInputCents), Money.centsToYuan(row.laborCents), Money.centsToYuan(row.directCostCents), Money.centsToYuan(row.profitCents) };
            } else {
                rows[i] = new Object[] { row.plotCode, row.managerName, Money.centsToYuan(row.incomeCents), Money.centsToYuan(row.directCostCents), Money.centsToYuan(row.profitCents) };
            }
        }
        return rows;
    }

    private JComboBox<Item> managerCombo() {
        JComboBox<Item> combo = new JComboBox<Item>();
        for (Manager row : store.managers) {
            combo.addItem(new Item(row.id, row.name));
        }
        return combo;
    }

    private JComboBox<Item> plotCombo() {
        JComboBox<Item> combo = new JComboBox<Item>();
        for (Plot row : store.plots) {
            combo.addItem(new Item(row.id, row.code + " - " + store.managerName(row.managerId)));
        }
        return combo;
    }

    private JComboBox<Item> materialCombo() {
        JComboBox<Item> combo = new JComboBox<Item>();
        combo.addItem(new Item("", "手动填写"));
        for (Material row : store.materials) {
            if (row.enabled) {
                combo.addItem(new Item(row.id, row.name));
            }
        }
        return combo;
    }

    private String selectedId(JComboBox<Item> combo) {
        Item item = (Item) combo.getSelectedItem();
        return item == null ? "" : item.id;
    }

    private void selectCombo(JComboBox<Item> combo, String id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).id.equals(id == null ? "" : id)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private PlotInput findPlotInput(String id) {
        for (PlotInput row : store.plotInputs) {
            if (row.id.equals(id)) {
                return row;
            }
        }
        return null;
    }

    private LaborRecord findLabor(String id) {
        for (LaborRecord row : store.laborRecords) {
            if (row.id.equals(id)) {
                return row;
            }
        }
        return null;
    }

    private Shipment findShipment(String id) {
        for (Shipment row : store.shipments) {
            if (row.id.equals(id)) {
                return row;
            }
        }
        return null;
    }

    private PublicExpense findPublicExpense(String id) {
        for (PublicExpense row : store.publicExpenses) {
            if (row.id.equals(id)) {
                return row;
            }
        }
        return null;
    }

    private FixedExpense findFixedExpense(String id) {
        for (FixedExpense row : store.fixedExpenses) {
            if (row.id.equals(id)) {
                return row;
            }
        }
        return null;
    }

    private int pageIndex(String key) {
        String[] keys = { "dashboard", "plots", "managers", "materials", "plotInputs", "labor", "shipments", "publicExpenses", "fixedExpenses", "profit", "export" };
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equals(key)) {
                return i;
            }
        }
        return -1;
    }

    private String today() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    private void showError(String title, Exception ex) {
        JOptionPane.showMessageDialog(frame, title + "\n" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
            // Keep Swing's default look and feel when the system one is unavailable.
        }
    }

    private static final class Item {
        final String id;
        final String label;

        Item(String id, String label) {
            this.id = id == null ? "" : id;
            this.label = label == null ? "" : label;
        }

        public String toString() {
            return label;
        }
    }
}
