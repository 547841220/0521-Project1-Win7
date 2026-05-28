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
import com.aimoney.fieldledger.win7.LedgerStore.ManagerExpenseDetail;
import com.aimoney.fieldledger.win7.LedgerStore.ManagerExpenseSummary;
import com.aimoney.fieldledger.win7.LedgerStore.ManagerPublicAverageSummary;
import com.aimoney.fieldledger.win7.LedgerStore.ManagerTeamLaborSummary;
import com.aimoney.fieldledger.win7.LedgerStore.TeamLaborSummary;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class FieldLedgerWin7App {
    private static final Color SIDEBAR = new Color(248, 249, 251);
    private static final Color SIDEBAR_ACTIVE = new Color(233, 239, 249);
    private static final Color SIDEBAR_HOVER = new Color(241, 244, 248);
    private static final Color PAPER = new Color(244, 245, 247);
    private static final Color PANEL = Color.WHITE;
    private static final Color TEXT = new Color(31, 35, 40);
    private static final Color MUTED = new Color(102, 112, 133);
    private static final Color BORDER = new Color(220, 225, 232);
    private static final Color ACCENT = new Color(10, 132, 255);
    private static final Color ACCENT_SOFT = new Color(229, 241, 255);
    private static final Color DANGER = new Color(180, 35, 24);
    private static final Font FONT_PLAIN = new Font("Microsoft YaHei", Font.PLAIN, 13);
    private static final Font FONT_BOLD = new Font("Microsoft YaHei", Font.BOLD, 13);

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
        frame.setMinimumSize(new Dimension(1040, 700));
        frame.getContentPane().setBackground(PAPER);
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
        panel.setPreferredSize(new Dimension(232, 700));
        panel.setBackground(SIDEBAR);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

        JPanel brand = new JPanel(new BorderLayout(12, 0));
        brand.setOpaque(false);
        brand.setBorder(BorderFactory.createEmptyBorder(24, 18, 22, 18));
        JLabel mark = new JLabel("田", JLabel.CENTER);
        mark.setOpaque(true);
        mark.setBackground(ACCENT);
        mark.setForeground(Color.WHITE);
        mark.setFont(new Font("Microsoft YaHei", Font.BOLD, 17));
        mark.setPreferredSize(new Dimension(42, 42));
        mark.setBorder(new RoundBorder(ACCENT, 10));
        JLabel name = new JLabel("<html><b>农业经营记账</b><br><span style='font-size:10px;color:#667085'>Win7兼容版</span></html>");
        name.setForeground(TEXT);
        name.setFont(FONT_BOLD);
        brand.add(mark, BorderLayout.WEST);
        brand.add(name, BorderLayout.CENTER);

        JPanel nav = new JPanel(new GridLayout(0, 1, 0, 4));
        nav.setOpaque(false);
        nav.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
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
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel topbar = panelBox(16);
        topbar.setLayout(new BorderLayout());
        JLabel season = new JLabel("2026年第一期");
        season.setForeground(MUTED);
        season.setFont(season.getFont().deriveFont(Font.BOLD, 12F));
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 28));
        title.setForeground(TEXT);
        JPanel titleBox = new JPanel(new BorderLayout());
        titleBox.setOpaque(false);
        titleBox.add(season, BorderLayout.NORTH);
        titleBox.add(title, BorderLayout.CENTER);

        status.setOpaque(true);
        status.setBackground(ACCENT_SOFT);
        status.setForeground(new Color(23, 83, 151));
        status.setFont(FONT_BOLD);
        status.setBorder(BorderFactory.createCompoundBorder(new RoundBorder(new Color(190, 219, 255), 14), BorderFactory.createEmptyBorder(7, 12, 7, 12)));
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
        button.setFont(FONT_BOLD);
        button.setForeground(MUTED);
        button.setBackground(SIDEBAR);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.putClientProperty("pageKey", key);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!key.equals(currentPage)) {
                    button.setBackground(SIDEBAR_HOVER);
                }
            }

            public void mouseExited(MouseEvent e) {
                if (!key.equals(currentPage)) {
                    button.setBackground(SIDEBAR);
                }
            }
        });
        button.addActionListener(e -> openPage(key));
        navButtons.add(button);
        nav.add(button);
    }

    private void openPage(String key) {
        currentPage = key;
        for (JButton button : navButtons) {
            button.setBackground(SIDEBAR);
            button.setForeground(MUTED);
        }
        int index = pageIndex(key);
        if (index >= 0 && index < navButtons.size()) {
            navButtons.get(index).setBackground(SIDEBAR_ACTIVE);
            navButtons.get(index).setForeground(TEXT);
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

        JTable table = table(new String[] { "地块", "管理人", "出货收入", "直接成本", "其他成本", "利润" }, profitTableRows(false));
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
        JButton detail = secondaryButton("查看明细");
        detail.addActionListener(e -> {
            if (selectedId[0].isEmpty()) {
                JOptionPane.showMessageDialog(frame, "请先选择一个管理人");
                return;
            }
            showManagerDetail(selectedId[0]);
        });
        form.add(detail, gbcWide(5, 1));

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
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    Manager manager = store.managers.get(table.getSelectedRow());
                    showManagerDetail(manager.id);
                }
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
        final JTextField teamLeader = field();
        final JTextField project = field();
        final JTextField maleCount = field("0");
        final JTextField malePrice = field("0");
        final JTextField femaleCount = field("0");
        final JTextField femalePrice = field("0");
        final JTextField vehicle = field("0");
        final JTextField note = field();
        final JTable table = table(new String[] { "日期", "团队头", "地块", "管理人", "项目", "男工数", "男工单价", "男工金额", "女工数", "女工单价", "女工金额", "车费", "合计", "备注" }, laborRows());
        final String[] selectedId = new String[] { "" };

        JPanel form = formPanel();
        addField(form, 0, "日期", date);
        addField(form, 1, "地块", plot);
        addField(form, 2, "团队头", teamLeader);
        addField(form, 3, "项目", project);
        addField(form, 4, "男工数", maleCount);
        addField(form, 5, "男工单价", malePrice);
        addField(form, 6, "女工数", femaleCount);
        addField(form, 7, "女工单价", femalePrice);
        addField(form, 8, "车费", vehicle);
        addField(form, 9, "备注", note);
        addActions(form, 10, new Runnable() {
            public void run() {
                LaborRecord row = selectedId[0].isEmpty() ? new LaborRecord(date.getText(), selectedId(plot), project.getText(), Money.parseDouble(maleCount.getText()), Money.yuanToCents(malePrice.getText()), Money.parseDouble(femaleCount.getText()), Money.yuanToCents(femalePrice.getText()), Money.yuanToCents(vehicle.getText()), teamLeader.getText(), note.getText()) : findLabor(selectedId[0]);
                row.date = date.getText();
                row.plotId = selectedId(plot);
                row.projectName = project.getText();
                row.maleCount = Money.parseDouble(maleCount.getText());
                row.malePriceCents = Money.yuanToCents(malePrice.getText());
                row.femaleCount = Money.parseDouble(femaleCount.getText());
                row.femalePriceCents = Money.yuanToCents(femalePrice.getText());
                row.vehicleAmountCents = Money.yuanToCents(vehicle.getText());
                row.teamLeader = teamLeader.getText();
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
                teamLeader.setText(row.teamLeader);
                project.setText(row.projectName);
                maleCount.setText(String.valueOf(row.maleCount));
                malePrice.setText(Money.centsInput(row.malePriceCents));
                femaleCount.setText(String.valueOf(row.femaleCount));
                femalePrice.setText(Money.centsInput(row.femalePriceCents));
                vehicle.setText(Money.centsInput(row.vehicleAmountCents));
                note.setText(row.note);
            }
        });
        JPanel page = pagePanel();
        JTabbedPane tableArea = new JTabbedPane();
        tableArea.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        tableArea.addTab("明细列表", wrapTable("明细列表", table));
        tableArea.addTab("工资统计", wrapTable("工资统计", table(new String[] { "管理人", "团队头", "男工数", "男工金额", "女工数", "女工金额", "车费", "合计" }, managerTeamLaborSummaryRows())));
        page.add(form, BorderLayout.NORTH);
        page.add(tableArea, BorderLayout.CENTER);
        return page;
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
        final JTextField netWeight = field();
        final JTextField price = field("0");
        final JTextField note = field();
        final JTable table = table(new String[] { "日期", "地块", "管理人", "车次", "毛重公斤", "毛重斤", "筐数", "单筐皮重", "总皮重", "扣除", "扣重", "自动净重斤", "净重斤", "单价", "总价", "备注" }, shipmentRows());
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
        addField(form, 8, "净重斤", netWeight);
        addField(form, 9, "单价", price);
        addField(form, 10, "备注", note);
        addActions(form, 11, new Runnable() {
            public void run() {
                Shipment row = selectedId[0].isEmpty() ? new Shipment(date.getText(), selectedId(plot), Money.parseInt(batch.getText()), Money.parseDouble(grossKg.getText()), Money.parseDouble(basketCount.getText()), Money.parseDouble(basketWeight.getText()), deductionName.getText(), Money.parseDouble(deductionWeight.getText()), Money.parseDouble(netWeight.getText()), Money.yuanToCents(price.getText()), note.getText()) : findShipment(selectedId[0]);
                row.date = date.getText();
                row.plotId = selectedId(plot);
                row.batchNo = Money.parseInt(batch.getText());
                row.grossWeightKg = Money.parseDouble(grossKg.getText());
                row.basketCount = Money.parseDouble(basketCount.getText());
                row.basketWeight = Money.parseDouble(basketWeight.getText());
                row.deductionName = deductionName.getText();
                row.deductionWeight = Money.parseDouble(deductionWeight.getText());
                row.manualNetWeightJin = Money.parseDouble(netWeight.getText());
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
                netWeight.setText(row.hasManualNetWeight() ? Money.number(row.manualNetWeightJin) : "");
                price.setText(Money.centsInput(row.unitPriceCents));
                note.setText(row.note);
            }
        });
        return crudPage(form, table);
    }

    private JPanel publicExpensesPage() {
        final JComboBox<String> accountType = optionCombo(LedgerStore.PUBLIC_EXPENSE_TYPES);
        final JTextField date = field(today());
        final JComboBox<String> category = optionCombo(LedgerStore.PUBLIC_EXPENSE_CATEGORIES);
        final JTextField name = field();
        final JTextField quantity = field("1");
        final JTextField unit = field();
        final JTextField price = field("0");
        final JTextField shareCount = field(String.valueOf(Math.max(1, store.managers.size())));
        final JTextField note = field();
        final JTable table = table(new String[] { "账目类型", "日期", "类别", "名称", "数量", "单位", "单价", "金额", "平摊家数", "备注" }, publicExpenseRows());
        final String[] selectedId = new String[] { "" };

        JPanel form = formPanel();
        addField(form, 0, "账目类型", accountType);
        addField(form, 1, "日期", date);
        addField(form, 2, "类别", category);
        addField(form, 3, "名称", name);
        addField(form, 4, "数量", quantity);
        addField(form, 5, "单位", unit);
        addField(form, 6, "单价", price);
        addField(form, 7, "平摊家数", shareCount);
        addField(form, 8, "备注", note);
        addActions(form, 9, new Runnable() {
            public void run() {
                PublicExpense row = selectedId[0].isEmpty() ? new PublicExpense(selectedString(accountType), date.getText(), selectedString(category), name.getText(), Money.parseDouble(quantity.getText()), unit.getText(), Money.yuanToCents(price.getText()), note.getText()) : findPublicExpense(selectedId[0]);
                row.accountType = selectedString(accountType);
                row.date = date.getText();
                row.category = selectedString(category);
                row.name = name.getText();
                row.quantity = Money.parseDouble(quantity.getText());
                row.unit = unit.getText();
                row.unitPriceCents = Money.yuanToCents(price.getText());
                row.shareCount = Money.parseInt(shareCount.getText());
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
                accountType.setSelectedItem(LedgerStore.publicType(row.accountType));
                date.setText(row.date);
                category.setSelectedItem(row.category);
                name.setText(row.name);
                quantity.setText(String.valueOf(row.quantity));
                unit.setText(row.unit);
                price.setText(Money.centsInput(row.unitPriceCents));
                shareCount.setText(String.valueOf(store.publicExpenseShareCount(row)));
                note.setText(row.note);
            }
        });
        JPanel page = pagePanel();
        JTabbedPane tableArea = new JTabbedPane();
        tableArea.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        tableArea.addTab("明细列表", wrapTable("明细列表", table));
        tableArea.addTab("分类汇总", wrapTable("分类汇总", table(new String[] { "账目类型", "类别", "金额" }, publicExpenseSummaryRows())));
        tableArea.addTab("管理人平摊", wrapTable("管理人平摊", table(new String[] { "管理人", "地块数", "公账平均分摊", "每地块其他成本" }, managerPublicAverageRows())));
        page.add(form, BorderLayout.NORTH);
        page.add(tableArea, BorderLayout.CENTER);
        return page;
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
        JTabbedPane tableArea = new JTabbedPane();
        tableArea.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        tableArea.addTab("地块利润核对", wrapTable("地块利润核对", table(new String[] { "地块", "管理人", "出货收入", "地块投入", "工资用工", "直接成本", "其他成本", "总成本", "利润" }, profitTableRows(true))));
        tableArea.addTab("团队工资汇总", wrapTable("团队工资汇总", table(new String[] { "团队头", "男工金额", "女工金额", "车费", "合计" }, teamLaborSummaryRows())));
        tableArea.addTab("公账分类汇总", wrapTable("公账分类汇总", table(new String[] { "账目类型", "类别", "金额" }, publicExpenseSummaryRows())));
        tableArea.addTab("管理人平摊", wrapTable("管理人平摊", table(new String[] { "管理人", "地块数", "公账平均分摊", "每地块其他成本" }, managerPublicAverageRows())));
        page.add(tableArea, BorderLayout.CENTER);

        JPanel bottom = panelBox();
        bottom.setLayout(new BorderLayout(12, 12));
        JPanel cards = new JPanel(new GridLayout(1, 6, 12, 12));
        cards.setOpaque(false);
        cards.add(metric("小组账合计/直接成本", Money.centsToYuan(summary.directCostCents)));
        cards.add(metric("其他成本", Money.centsToYuan(summary.otherCostCents)));
        cards.add(metric("普通公账", Money.centsToYuan(summary.publicNormalExpenseCents)));
        cards.add(metric("公账平均", Money.centsToYuan(summary.publicAverageExpenseCents)));
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
        JLabel text = new JLabel("<html><h2>导出 Excel 可打开的 CSV 文件</h2><p>会生成地块利润、地块投入、工资用工、出货记录、公账、固定账、总账核对、公账分类汇总、团队工资汇总、管理人工资统计共 10 个文件。</p><p>数据目录: " + store.dataDir.getAbsolutePath() + "</p></html>");
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
        JButton delete = dangerButton("删除选中");
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
        return panelBox(20);
    }

    private JPanel panelBox(int padding) {
        JPanel panel = new SurfacePanel(PANEL, 14);
        panel.setBorder(BorderFactory.createCompoundBorder(new RoundBorder(BORDER, 14), BorderFactory.createEmptyBorder(padding, padding, padding, padding)));
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
        label.setFont(new Font("Microsoft YaHei", Font.BOLD, 17));
        label.setForeground(TEXT);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new RoundBorder(new Color(232, 235, 241), 10));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(label, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel metric(String label, String value) {
        JPanel panel = panelBox();
        panel.setLayout(new BorderLayout());
        JLabel name = new JLabel(label);
        name.setForeground(MUTED);
        name.setFont(FONT_BOLD);
        JLabel amount = new JLabel(value);
        amount.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        amount.setForeground(TEXT);
        amount.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(name, BorderLayout.NORTH);
        panel.add(amount, BorderLayout.CENTER);
        return panel;
    }

    private JTextField field() {
        return field("");
    }

    private JTextField field(String value) {
        JTextField field = new JTextField(value);
        field.setPreferredSize(new Dimension(148, 34));
        field.setFont(FONT_PLAIN);
        field.setForeground(TEXT);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(new RoundBorder(new Color(205, 213, 224), 9), BorderFactory.createEmptyBorder(5, 9, 5, 9)));
        return field;
    }

    private void addField(JPanel form, int row, String label, Component input) {
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));
        fieldLabel.setForeground(MUTED);
        fieldLabel.setFont(FONT_BOLD);
        if (input instanceof JComboBox) {
            input.setFont(FONT_PLAIN);
            input.setBackground(Color.WHITE);
        } else if (input instanceof JCheckBox) {
            input.setFont(FONT_PLAIN);
            input.setBackground(PANEL);
            input.setForeground(TEXT);
        }
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
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createCompoundBorder(new RoundBorder(ACCENT, 10), BorderFactory.createEmptyBorder(7, 16, 7, 16)));
        button.setFont(FONT_BOLD);
        button.setBackground(ACCENT);
        button.setForeground(Color.WHITE);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton secondaryButton(String label) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createCompoundBorder(new RoundBorder(BORDER, 10), BorderFactory.createEmptyBorder(7, 16, 7, 16)));
        button.setFont(FONT_BOLD);
        button.setBackground(new Color(248, 250, 252));
        button.setForeground(TEXT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton dangerButton(String label) {
        JButton button = secondaryButton(label);
        button.setBorder(BorderFactory.createCompoundBorder(new RoundBorder(new Color(254, 205, 202), 10), BorderFactory.createEmptyBorder(7, 16, 7, 16)));
        button.setBackground(new Color(255, 241, 240));
        button.setForeground(DANGER);
        return button;
    }

    private JTable table(String[] headers, Object[][] rows) {
        JTable table = new JTable(new DefaultTableModel(rows, headers) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        }) {
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) {
                    component.setBackground(ACCENT_SOFT);
                    component.setForeground(TEXT);
                } else {
                    component.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    component.setForeground(TEXT);
                }
                return component;
            }
        };
        table.setFont(FONT_PLAIN.deriveFont(14F));
        table.setForeground(TEXT);
        table.setGridColor(new Color(235, 238, 243));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setRowHeight(40);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(ACCENT_SOFT);
        table.setSelectionForeground(TEXT);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setForeground(MUTED);
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.LEFT);
        for (int i = 0; i < headers.length; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(tableColumnWidth(headers[i]));
        }
        return table;
    }

    private int tableColumnWidth(String header) {
        if ("日期".equals(header)) {
            return 120;
        }
        if ("备注".equals(header)) {
            return 180;
        }
        if (header.indexOf("金额") >= 0 || header.indexOf("单价") >= 0 || header.indexOf("合计") >= 0 || header.indexOf("成本") >= 0) {
            return 120;
        }
        if (header.length() >= 4) {
            return 110;
        }
        return 95;
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
        Object[][] rows = new Object[store.laborRecords.size()][14];
        for (int i = 0; i < store.laborRecords.size(); i++) {
            LaborRecord row = store.laborRecords.get(i);
            rows[i] = new Object[] { row.date, store.laborTeamLeader(row), store.plotCode(row.plotId), store.plotManagerName(row.plotId), row.projectName, Money.number(row.maleCount), Money.centsToYuan(row.malePriceCents), Money.centsToYuan(row.maleAmountCents()), Money.number(row.femaleCount), Money.centsToYuan(row.femalePriceCents), Money.centsToYuan(row.femaleAmountCents()), Money.centsToYuan(row.vehicleAmountCents), Money.centsToYuan(row.totalCents()), row.note };
        }
        return rows;
    }

    private Object[][] shipmentRows() {
        Object[][] rows = new Object[store.shipments.size()][16];
        for (int i = 0; i < store.shipments.size(); i++) {
            Shipment row = store.shipments.get(i);
            rows[i] = new Object[] { row.date, store.plotCode(row.plotId), store.plotManagerName(row.plotId), row.batchNo, Money.number(row.grossWeightKg), Money.number(row.grossWeightJin()), Money.number(row.basketCount), Money.number(row.basketWeight), Money.number(row.tareWeight()), row.deductionName, Money.number(row.deductionWeight), Money.number(row.autoNetWeightJin()), Money.number(row.netWeightJin()), Money.centsToYuan(row.unitPriceCents), Money.centsToYuan(row.totalCents()), row.note };
        }
        return rows;
    }

    private Object[][] teamLaborSummaryRows() {
        List<TeamLaborSummary> summaries = store.teamLaborSummaries();
        Object[][] rows = new Object[summaries.size()][5];
        for (int i = 0; i < summaries.size(); i++) {
            TeamLaborSummary row = summaries.get(i);
            rows[i] = new Object[] { row.teamLeader, Money.centsToYuan(row.maleAmountCents), Money.centsToYuan(row.femaleAmountCents), Money.centsToYuan(row.vehicleAmountCents), Money.centsToYuan(row.totalCents) };
        }
        return rows;
    }

    private Object[][] managerTeamLaborSummaryRows() {
        List<ManagerTeamLaborSummary> summaries = store.managerTeamLaborSummaries();
        Object[][] rows = new Object[summaries.size()][8];
        for (int i = 0; i < summaries.size(); i++) {
            ManagerTeamLaborSummary row = summaries.get(i);
            rows[i] = new Object[] { row.managerName, row.teamLeader, Money.number(row.maleCount), Money.centsToYuan(row.maleAmountCents), Money.number(row.femaleCount), Money.centsToYuan(row.femaleAmountCents), Money.centsToYuan(row.vehicleAmountCents), Money.centsToYuan(row.totalCents) };
        }
        return rows;
    }

    private Object[][] publicExpenseRows() {
        Object[][] rows = new Object[store.publicExpenses.size()][10];
        for (int i = 0; i < store.publicExpenses.size(); i++) {
            PublicExpense row = store.publicExpenses.get(i);
            rows[i] = new Object[] { LedgerStore.publicType(row.accountType), row.date, row.category, row.name, Money.number(row.quantity), row.unit, Money.centsToYuan(row.unitPriceCents), Money.centsToYuan(row.totalCents()), store.publicExpenseShareCount(row) == 0 ? "" : String.valueOf(store.publicExpenseShareCount(row)), row.note };
        }
        return rows;
    }

    private Object[][] managerPublicAverageRows() {
        List<ManagerPublicAverageSummary> summaries = store.managerPublicAverageSummaries();
        Object[][] rows = new Object[summaries.size()][4];
        for (int i = 0; i < summaries.size(); i++) {
            ManagerPublicAverageSummary row = summaries.get(i);
            rows[i] = new Object[] { row.managerName, row.plotCount, Money.centsToYuan(row.publicAverageCents), Money.centsToYuan(row.perPlotOtherCostCents) };
        }
        return rows;
    }

    private Object[][] publicExpenseSummaryRows() {
        List<String[]> summaryRows = store.publicExpenseSummaryRows();
        Object[][] rows = new Object[summaryRows.size()][3];
        for (int i = 0; i < summaryRows.size(); i++) {
            String[] row = summaryRows.get(i);
            rows[i] = new Object[] { row[0], row[1], row[2] };
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
        Object[][] rows = new Object[profits.size()][full ? 9 : 6];
        for (int i = 0; i < profits.size(); i++) {
            PlotProfit row = profits.get(i);
            if (full) {
                rows[i] = new Object[] { row.plotCode, row.managerName, Money.centsToYuan(row.incomeCents), Money.centsToYuan(row.plotInputCents), Money.centsToYuan(row.laborCents), Money.centsToYuan(row.directCostCents), Money.centsToYuan(row.otherCostCents), Money.centsToYuan(row.totalCostCents), Money.centsToYuan(row.profitCents) };
            } else {
                rows[i] = new Object[] { row.plotCode, row.managerName, Money.centsToYuan(row.incomeCents), Money.centsToYuan(row.directCostCents), Money.centsToYuan(row.otherCostCents), Money.centsToYuan(row.profitCents) };
            }
        }
        return rows;
    }

    private void showManagerDetail(String managerId) {
        Manager manager = store.findManager(managerId);
        if (manager == null) {
            JOptionPane.showMessageDialog(frame, "未找到管理人");
            return;
        }

        ManagerExpenseSummary summary = store.managerExpenseSummary(managerId);
        JDialog dialog = new JDialog(frame, manager.name + " 明细", true);
        dialog.setLayout(new BorderLayout(12, 12));

        JPanel cards = new JPanel(new GridLayout(1, 5, 12, 12));
        cards.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        cards.add(metric("地块数", String.valueOf(summary.plotCount)));
        cards.add(metric("地块投入", Money.centsToYuan(summary.plotInputCents)));
        cards.add(metric("工资用工", Money.centsToYuan(summary.laborCents)));
        cards.add(metric("公账平均分摊", Money.centsToYuan(summary.publicAverageCents)));
        cards.add(metric("合计", Money.centsToYuan(summary.totalCents)));

        JTable detailTable = table(new String[] { "日期", "来源", "地块/平摊", "类别/团队", "名称/项目", "金额", "备注" }, managerExpenseDetailRows(managerId));
        dialog.add(cards, BorderLayout.NORTH);
        dialog.add(wrapTable("费用去向明细", detailTable), BorderLayout.CENTER);

        JButton close = secondaryButton("关闭");
        close.addActionListener(e -> dialog.dispose());
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));
        footer.add(close, BorderLayout.EAST);
        dialog.add(footer, BorderLayout.SOUTH);

        dialog.setSize(new Dimension(980, 620));
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private Object[][] managerExpenseDetailRows(String managerId) {
        List<ManagerExpenseDetail> details = store.managerExpenseDetails(managerId);
        Object[][] rows = new Object[details.size()][7];
        for (int i = 0; i < details.size(); i++) {
            ManagerExpenseDetail row = details.get(i);
            rows[i] = new Object[] { row.date, row.source, row.target, row.category, row.name, Money.centsToYuan(row.amountCents), row.note };
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

    private JComboBox<String> optionCombo(String[] values) {
        JComboBox<String> combo = new JComboBox<String>(values);
        combo.setEditable(true);
        return combo;
    }

    private String selectedString(JComboBox<String> combo) {
        Object value = combo.getSelectedItem();
        return value == null ? "" : value.toString();
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
            UIManager.put("Panel.background", PAPER);
            UIManager.put("OptionPane.background", PAPER);
            UIManager.put("OptionPane.messageForeground", TEXT);
            UIManager.put("TabbedPane.font", FONT_BOLD);
            UIManager.put("TabbedPane.selected", Color.WHITE);
            UIManager.put("TextField.font", FONT_PLAIN);
            UIManager.put("ComboBox.font", FONT_PLAIN);
            UIManager.put("CheckBox.font", FONT_PLAIN);
        } catch (Exception ignored) {
            // Keep Swing's default look and feel when the system one is unavailable.
        }
    }

    private static final class SurfacePanel extends JPanel {
        private final int radius;

        SurfacePanel(Color background, int radius) {
            this.radius = radius;
            setOpaque(false);
            setBackground(background);
        }

        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int radius;

        RoundBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        public Insets getBorderInsets(Component component) {
            return new Insets(1, 1, 1, 1);
        }

        public Insets getBorderInsets(Component component, Insets insets) {
            insets.left = 1;
            insets.top = 1;
            insets.right = 1;
            insets.bottom = 1;
            return insets;
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
