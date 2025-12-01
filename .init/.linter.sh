#!/bin/bash
cd /home/kavia/workspace/code-generation/online-chess-platform-179904-179913/chess_game_backend
./gradlew checkstyleMain
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

