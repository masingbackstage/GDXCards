#!/usr/bin/env python3

import subprocess
import os
import time

folder_path = r'C:\Users\Filip\IdeaProjects\GDXCards\lwjgl3\build\libs'  
command = r'java -jar .\GDXCARDS-1.0.0.jar'  
gradle_command = r'gradlew.bat lwjgl3:dist'  


os.chdir(r'C:\Users\Filip\IdeaProjects\GDXCards') 

print("Starting Gradle, plz wait...")
gradle_process = subprocess.Popen(gradle_command, shell=True)
gradle_process.wait()

print("Gradle finished, starting pwsh...")

def set_window_position(x, y, width, height):
    return f'''
    $h = Get-Process -id $pid | Select-Object -ExpandProperty MainWindowHandle
    Add-Type -TypeDefinition @"
    using System;
    using System.Runtime.InteropServices;
    public class Window {{
        [DllImport("user32.dll")]
        public static extern bool MoveWindow(IntPtr hWnd, int x, int y, int width, int height, bool repaint);
    }}
    "@
    [Window]::MoveWindow($h, {x}, {y}, {width}, {height}, $true)
    '''

left_window_position = (0, 100, 960, 800) 
right_window_position = (960, 100, 960, 800) 

subprocess.Popen(['start', 'powershell', '-NoExit', '-Command', f'cd "{folder_path}" ; {command} ; Start-Sleep -Seconds 1 ; {set_window_position(*left_window_position)}'], shell=True)

time.sleep(2)

subprocess.Popen(['start', 'powershell', '-NoExit', '-Command', f'cd "{folder_path}" ; {command} ; Start-Sleep -Seconds 1 ; {set_window_position(*right_window_position)}'], shell=True)
