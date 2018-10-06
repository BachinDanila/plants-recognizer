# -*- coding: utf8 -*-
import xlrd, xlwt
import sqlite3
import os

#открываем файл
rb = xlrd.open_workbook('Определитель.xlsx')

#выбираем активный лист
sheet = rb.sheet_by_index(0)

answers = []

s = ""

params = sheet.row_values(0)

answers_raw = [sheet.row_values(rownum) for rownum in range(1,sheet.nrows)]

names = [sheet.row_values(rownum)[0] for rownum in range(1,sheet.nrows)]

for l in answers_raw:
    for i in l:
        
        if i == '':
            i = "-"
            
        if type(i) == float:
            i = int(i)
            
        s += str(i) + ";"
    answers.append(s)
    s = ""
    
all_questions = "All_Questions.db"
conn = sqlite3.connect(all_questions) # или :memory: чтобы сохранить в RAM
cursor = conn.cursor()

sql_delete = """
DELETE FROM answers;
DELETE FROM params;
DELETE FROM names;
"""
cursor.executescript(sql_delete)
conn.commit()
conn.close()

os.remove(all_questions)
conn = sqlite3.connect(all_questions) # или :memory: чтобы сохранить в RAM
cursor = conn.cursor()

cursor.execute("""CREATE TABLE answers(
                      ID INTEGER PRIMARY KEY AUTOINCREMENT,
                      answers TEXT NOT NULL)""")

cursor.execute("""CREATE TABLE params(
                      ID INTEGER PRIMARY KEY AUTOINCREMENT,
                      params TEXT NOT NULL)""")

cursor.execute("""CREATE TABLE names(
                      ID INTEGER PRIMARY KEY AUTOINCREMENT,
                      names TEXT NOT NULL)""")

conn.commit()

for i in answers:
    cursor.execute("INSERT INTO answers(answers) VALUES (?)",(i,))
    
for i in params:
    cursor.execute("INSERT INTO params(params) VALUES (?)", (i,))

for i in names:
    cursor.execute("INSERT INTO names(names) VALUES (?)", (i,))

conn.commit()
conn.close()







