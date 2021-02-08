from random import randrange  # для завдання 2
from math import ceil  # для завдання 2

students_str = "Дмитренко Олександр - ІП-84; Матвійчук Андрій - ІВ-83; Лесик Сергій - ІО-82; Ткаченко Ярослав - ІВ-83; " \
              "Аверкова Анастасія - ІО-83; Соловйов Даніїл - ІО-83; Рахуба Вероніка - ІО-81; Кочерук Давид - ІВ-83; " \
              "Лихацька Юлія - ІВ-82; Головенець Руслан - ІВ-83; Ющенко Андрій - ІО-82; Мінченко Володимир - ІП-83; " \
              "Мартинюк Назар - ІО-82; Базова Лідія - ІВ-81; Снігурець Олег - ІВ-81; Роман Олександр - ІО-82; Дудка " \
              "Максим - ІО-81; Кулініч Віталій - ІВ-81; Жуков Михайло - ІП-83; Грабко Михайло - ІВ-81; Іванов " \
              "Володимир - ІО-81; Востриков Нікіта - ІО-82; Бондаренко Максим - ІВ-83; Скрипченко Володимир - ІВ-82; " \
              "Кобук Назар - ІО-81; Дровнін Павло - ІВ-83; Тарасенко Юлія - ІО-82; Дрозд Світлана - ІВ-81; Фещенко " \
              "Кирил - ІО-82; Крамар Віктор - ІО-83; Іванов Дмитро - ІВ-82"

# Завдання 1
# Заповніть словник, де:
#   - ключ – назва групи
#   - значення – відсортований масив студентів, які відносяться до відповідної групи

students_groups = {}

for student in students_str.split("; "):
    st_info = student.split(" - ")
    students_groups.setdefault(st_info[1], []).append(st_info[0])

for i in students_groups.values():
    i.sort()

print("\tЗавдання 1:\n", students_groups)


# Завдання 2
# Заповніть словник, де:
#   - ключ – назва групи
#   - значення – словник, де:
#   - ключ – студент, який відносяться до відповідної групи
#   - значення – масив з оцінками студента (заповніть масив випадковими значеннями використовуючи функцію randomValue)

points = [12, 12, 12, 12, 12, 12, 12, 16]


def random_value(max_value):
    r = randrange(6)
    if r == 1: return ceil(max_value * 0.7)
    elif r == 2: return ceil(max_value * 0.9)
    elif r == 3 or r == 4 or r == 5: return max_value
    else: return 0


student_points = {}

for group, students in students_groups.items():
    student_marks = {}
    for student in students:
        student_marks[student] = [random_value(points[i]) for i in range(len(points))]
    student_points[group] = student_marks

print("\n\tЗавдання 2:\n", student_points)

# Завдання 3
# Заповніть словник, де:
#   - ключ – назва групи
#   - значення – словник, де:
#   - ключ – студент, який відносяться до відповідної групи
#   - значення – сума оцінок студента

sum_points = {}

for group, students in student_points.items():
    marks_sum = {}
    for student, marks in students.items():
        marks_sum[student] = sum(marks)
    sum_points[group] = marks_sum

print("\n\tЗавдання 3:\n", sum_points)

# Завдання 4
# Заповніть словник, де:
#   - ключ – назва групи
#   - значення – середня оцінка всіх студентів групи

group_avg = {}

for group, students_marks in sum_points.items():
    avg = sum(students_marks.values()) / len(students_marks.values())
    group_avg[group] = avg

print("\n\tЗавдання 4:\n", group_avg)

# Завдання 5
# Заповніть словник, де:
#   - ключ – назва групи
#   - значення – масив студентів, які мають >= 60 балів

passed_per_group = {}

for group, students_marks in sum_points.items():
    passed_per_group[group] = list(filter(lambda student: students_marks[student] > 60, students_marks.keys()))

print("\n\tЗавдання 5:\n", passed_per_group)
