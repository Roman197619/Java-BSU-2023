package by.Roman191976.Quizer;

/**
 * Enum, который описывает результат ответа на задание
 */
enum Result {
    OK, // Получен правильный ответ
    WRONG, // Получен неправильный ответ
    INCORRECT_INPUT // Некорректный ввод. Например, текст, когда ожидалось число
}