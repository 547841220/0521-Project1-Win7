#define MyAppName "农业经营记账Win7版"
#define MyAppVersion "0.1.0"
#define MyAppPublisher "AiMoney"
#define MyAppExeName "run-app.bat"

[Setup]
AppId={{A6E84D90-4698-4F47-B4D2-7E4D5406D501}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyAppPublisher}
DefaultDirName={localappdata}\AiMoneyFieldLedgerWin7
DefaultGroupName={#MyAppName}
DisableProgramGroupPage=yes
OutputDir=..\package\output
OutputBaseFilename=农业经营记账-Win7版-安装包
Compression=lzma
SolidCompression=yes
WizardStyle=modern
PrivilegesRequired=lowest
ArchitecturesAllowed=x64
ArchitecturesInstallIn64BitMode=x64
UninstallDisplayName={#MyAppName}

[Languages]
Name: "chinesesimp"; MessagesFile: "compiler:Languages\ChineseSimplified.isl"

[Files]
Source: "..\dist\field-ledger-win7.jar"; DestDir: "{app}\app"; Flags: ignoreversion
Source: "..\package\run-app.bat"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\package\jre\*"; DestDir: "{app}\jre"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{autodesktop}\农业经营记账Win7版"; Filename: "{app}\{#MyAppExeName}"; WorkingDir: "{app}"
Name: "{group}\农业经营记账Win7版"; Filename: "{app}\{#MyAppExeName}"; WorkingDir: "{app}"

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "启动农业经营记账Win7版"; Flags: postinstall skipifsilent nowait
