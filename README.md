# 农业经营记账 Win7 兼容版

这是一个独立的 Windows 7 兼容版本，不依赖原有 Tauri 项目运行，也不修改 `0521-Project1-FREE`。

## 技术路线

- Java 8
- Swing 桌面界面
- 本地文本文件存储
- CSV 导出，Excel 可打开
- 不依赖 WebView2、Tauri、Node、Rust

## 已覆盖功能

- 首页概览
- 地块管理
- 管理人管理
- 物料价格
- 地块投入
- 工资用工
- 出货记录
- 公账支出
- 固定账
- 利润核对
- Excel/CSV 导出

计算口径和当前正式版保持一致：

- 出货收入 = 出货记录总价合计
- 地块投入金额 = 数量 * 单价
- 工资用工金额 = 男工金额 + 女工金额 + 车费
- 直接成本 = 地块投入 + 工资用工
- 地块利润 = 出货收入 - 直接成本
- 公账支出 = 普通公账 + 公账平均
- 经营总开销 = 直接成本 + 公账支出 + 固定账

公账支出已按客户 Excel 细化：

- 账目类型：普通公账、公账平均
- 费用分类：日常生活、电费、材料、药、肥、油耗、固定消耗、其他
- 明细字段：日期、名称、数量、单位、单价、金额、备注
- 利润核对页会显示公账分类汇总
- 导出会额外生成 `08-公账分类汇总.csv`
- 工资用工页会显示按管理人和团队头归类的工资统计
- 导出会额外生成 `10-管理人工资统计.csv`

## 数据位置

Windows 下默认存放在：

```text
%APPDATA%\AiMoneyFieldLedgerWin7
```

导出文件会生成在：

```text
%APPDATA%\AiMoneyFieldLedgerWin7\exports
```

## Windows 7 构建

在安装 Java 8 JDK 的 Windows 电脑上双击：

```text
build-windows.bat
```

生成文件：

```text
dist\field-ledger-win7.jar
```

运行：

```text
start-windows.bat
```

## 给客户试用

最简交付可以发：

- `field-ledger-win7.jar`
- `start-windows.bat`

客户电脑需要能运行 Java 8。如果客户电脑没有 Java 8，后续可以再做“内置 JRE 的免安装包”。

## 推荐交付：安装包

客户什么都不用配置时，推荐生成一个安装包：

```text
package\output\农业经营记账-Win7版-安装包.exe
```

客户只需要双击安装，安装完成后桌面会出现：

```text
农业经营记账Win7版
```

打包电脑需要提前准备：

- Java 8 JDK，用来编译程序。
- Windows x64 Java 8 JRE，放到 `package\jre`。
- Inno Setup 6，用来生成安装包。

打包步骤：

1. 双击 `build-windows.bat`。
2. 确认生成 `dist\field-ledger-win7.jar`。
3. 把 Windows x64 Java 8 JRE 放到 `package\jre`，确保存在 `package\jre\bin\javaw.exe`。
4. 双击 `package-windows-installer.bat`。
5. 把 `package\output\农业经营记账-Win7版-安装包.exe` 发给客户。

客户不需要单独安装 Java。

## 推荐打包方式：GitHub Actions

Win7 兼容版建议单独建一个 GitHub 仓库，例如：

```text
0521-Project1-Win7
```

这个目录已经按独立仓库准备好了 GitHub Actions 工作流：

```text
.github/workflows/windows7-build.yml
```

使用方式：

1. 把代码推送到 GitHub。
2. 打开 GitHub 仓库的 `Actions` 页面。
3. 选择 `Build Win7 Compatible Installer`。
4. 点击 `Run workflow`。
5. 等构建完成后，在页面底部下载 artifact：

```text
windows7-compatible-installer
```

里面就是给客户的安装包：

```text
农业经营记账-Win7版-安装包.exe
```

客户侧最终仍然只需要这一个 `.exe` 文件。

安装包默认安装到当前用户目录，不要求客户右键管理员：

```text
%LOCALAPPDATA%\AiMoneyFieldLedgerWin7
```

注意：Actions 会把 Java 8 一起打进安装包。第一次交付前，仍建议在一台真实 Windows 7 电脑上安装打开一次，确认内置 Java 运行环境在该机器上可用。

```
cd /Users/jijie/Desktop/ai-project/ai-money/0521-Project1-WIN7
javac -encoding UTF-8 -d /tmp/fieldledger-win7-preview src/com/aimoney/fieldledger/win7/*.java
java -cp /tmp/fieldledger-win7-preview com.aimoney.fieldledger.win7.FieldLedgerWin7App
```
