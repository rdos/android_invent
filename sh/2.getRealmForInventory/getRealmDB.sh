#!/bin/bash
set -x
cd /home/rdos/D/android_inventory/sh/2.getRealmForInventory
rm INVENTORY.* -Rf 
/home/rdos/Android/Sdk/platform-tools/adb -d shell "run-as ru.smartro.inventory cat /data/data/ru.smartro.inventory/files/INVENTORY.realm" > INVENTORY.realm