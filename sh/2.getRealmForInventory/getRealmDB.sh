#!/bin/bash
set -x
cd /home/rdos/D/android_inventory/sh/2.getRealmForInventory
rm -Rf  INVENTORY.* 
/home/rdos/Android/Sdk/platform-tools/adb -d shell "run-as ru.smartro.inventory.stage cat /data/data/ru.smartro.inventory.stage/files/INVENTORY.realm" > INVENTORY.realm