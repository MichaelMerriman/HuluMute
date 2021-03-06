; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{AD4257CC-F747-4B97-8970-427E887EE3B3}
AppName=HuluMute
AppVersion=0.6
;AppVerName=HuluMute 0.6
AppPublisher=Michael Merriman
AppPublisherURL=https://dl.dropbox.com/u/51913736/HuluMute/index.html
AppSupportURL=https://dl.dropbox.com/u/51913736/HuluMute/index.html
AppUpdatesURL=https://dl.dropbox.com/u/51913736/HuluMute/index.html
DefaultDirName={pf}\HuluMute
DefaultGroupName=My Program
AllowNoIcons=yes
OutputBaseFilename=hulumute_setup_0.6
SetupIconFile=C:\Users\Mickey\Dropbox\Computer\NetBeansProjects\HuluMute\install and exe\inno setup\hulumute.ico
Compression=lzma
SolidCompression=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "C:\Users\Mickey\Dropbox\Computer\NetBeansProjects\HuluMute\hulumute.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Mickey\Dropbox\Computer\NetBeansProjects\HuluMute\cmdow.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Mickey\Dropbox\Computer\NetBeansProjects\HuluMute\hulumute.conf"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Mickey\Dropbox\Computer\NetBeansProjects\HuluMute\hulumute.png"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Mickey\Dropbox\Computer\NetBeansProjects\HuluMute\nircmdc.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Mickey\Dropbox\Computer\NetBeansProjects\HuluMute\src\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{group}\HuluMute"; Filename: "{app}\hulumute.exe"
Name: "{group}\{cm:UninstallProgram,HuluMute}"; Filename: "{uninstallexe}"
Name: "{commondesktop}\HuluMute"; Filename: "{app}\hulumute.exe"; Tasks: desktopicon

[Run]
Filename: "{app}\hulumute.exe"; Description: "{cm:LaunchProgram,HuluMute}"; Flags: nowait postinstall skipifsilent

