/*
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018-2021
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Gryzlov <nixel2007@gmail.com>, Sergey Batanov <sergey.batanov@dmpas.ru>
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * BSL Parser is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * BSL Parser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BSL Parser.
 */
package com.github._1c_syntax.bsl.parser.description;

import com.github._1c_syntax.bsl.parser.BSLParser;
import com.github._1c_syntax.bsl.parser.BSLTokenizer;
import com.github._1c_syntax.bsl.parser.description.support.ParameterDescription;
import com.github._1c_syntax.bsl.parser.description.support.SimpleRange;
import com.github._1c_syntax.bsl.parser.description.support.TypeDescription;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;
import utils.TestUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class BSLDescriptionReaderTest {

  @Test
  void parseMethodDescription() {
    var filePath = "src/test/resources/methodDescription/example1.bsl";
    var exampleString = TestUtils.getSourceFromFile(filePath);
    var tokens = getTokensFromString(exampleString);
    var methodDescription = BSLDescriptionReader.parseMethodDescription(tokens);

    assertThat(methodDescription).isNotNull();
    assertThat(methodDescription.getDescription()).isEqualTo(exampleString);
    assertThat(methodDescription.getPurposeDescription()).isEqualTo("Запустить выполнение процедуры в фоновом задании, если это возможно.\n" +
      "\n" +
      "При выполнении любого из следующих условий запуск выполняется не в фоне, а сразу в основном потоке:\n" +
      "* если вызов выполняется в файловой базе во внешнем соединении (в этом режиме фоновые задания не поддерживаются);\n" +
      "* если приложение запущено в режиме отладки (параметр /C РежимОтладки) - для упрощения отладки конфигурации;\n" +
      "* если в файловой ИБ имеются активные фоновые задания - для снижения времени ожидания пользователя;\n" +
      "* если выполняется процедура модуля внешней обработки или внешнего отчета.\n" +
      "\n" +
      "Не следует использовать эту функцию, если необходимо безусловно запускать фоновое задание.\n" +
      "Может применяться совместно с функцией ДлительныеОперацииКлиент.ОжидатьЗавершение.\n" +
      "\n" +
      "Вызываемая процедура может быть с произвольным числом параметров, но не более 7.\n" +
      "Значения передаваемых параметров процедуры, а также возвращаемое значение должны быть сериализуемыми.\n" +
      "Параметры процедуры не должны быть возвращаемыми.");
    assertThat(methodDescription.getCallOptions()).isEmpty();
    assertThat(methodDescription.getDeprecationInfo()).isEmpty();
    assertThat(methodDescription.getExamples()).hasSize(26)
      .anyMatch("В общем виде процесс запуска и обработки результата длительной операции в модуле формы выглядит следующим образом:"::equals)
      .anyMatch("2) Запуск операции на сервере и подключение обработчика ожидания (при необходимости):"::equals)
      .anyMatch("ПараметрыОжидания = ДлительныеОперацииКлиент.ПараметрыОжидания(ЭтотОбъект);"::equals)
      .anyMatch("Функция НачатьВыполнениеНаСервере()"::equals)
      .anyMatch("ПриЗавершенииРасчета();"::equals)
    ;
    assertThat(methodDescription.getLink()).isEmpty();
    assertThat(methodDescription.getParameters()).hasSize(9);
    checkParameter(methodDescription.getParameters().get(0),
      "ПараметрыВыполнения", 1, "", false);
    checkType(methodDescription.getParameters().get(0).getTypes().get(0),
      "см. ДлительныеОперации.ПараметрыВыполненияПроцедуры",
      "", 0, "ДлительныеОперации.ПараметрыВыполненияПроцедуры", true);
    checkParameter(methodDescription.getParameters().get(1), "ИмяПроцедуры", 1, "", false);
    checkType(methodDescription.getParameters().get(1).getTypes().get(0),
      "Строка", "имя экспортной процедуры общего модуля, модуля менеджера объекта\n" +
        "или модуля обработки, которую необходимо выполнить в фоне.\n" +
        "Например, \"МойОбщийМодуль.МояПроцедура\", \"Отчеты.ЗагруженныеДанные.Сформировать\"\n" +
        "или \"Обработки.ЗагрузкаДанных.МодульОбъекта.Загрузить\".", 0, "", false);
    checkParameter(methodDescription.getParameters().get(2), "Параметр1", 1, "", false);
    checkType(methodDescription.getParameters().get(2).getTypes().get(0),
      "Произвольный", "произвольные параметры вызова процедуры. Количество параметров может быть от 0 до 7.",
      0, "", false);
    checkParameter(methodDescription.getParameters().get(3), "Параметр2", 1, "", false);
    checkParameter(methodDescription.getParameters().get(4), "Параметр3", 1, "", false);
    checkParameter(methodDescription.getParameters().get(7), "Параметр6", 1, "", false);
    checkParameter(methodDescription.getParameters().get(8), "Параметр7", 1, "", false);
    checkType(methodDescription.getParameters().get(8).getTypes().get(0),
      "Произвольный", "", 0, "", false);

    assertThat(
      Objects.equals(methodDescription.getSimpleRange(), create(75, 20)))
      .isTrue();
    assertThat(methodDescription.getReturnedValue()).hasSize(1);
    checkType(methodDescription.getReturnedValue().get(0), "Структура", "параметры выполнения задания:",
      5, "", false);
    checkParameter(methodDescription.getReturnedValue().get(0).getParameters().get(0),
      "Статус", 1, "", false);
    checkType(methodDescription.getReturnedValue().get(0).getParameters().get(0).getTypes().get(0),
      "Строка", "\"Выполняется\", если задание еще не завершилось;\n" +
        "\"Выполнено\", если задание было успешно выполнено;\n" +
        "\"Ошибка\", если задание завершено с ошибкой;\n" +
        "\"Отменено\", если задание отменено пользователем или администратором.",
      0, "", false);
  }

  @Test
  void parseMethodDescription2() {
    var filePath = "src/test/resources/methodDescription/example2.bsl";
    var exampleString = TestUtils.getSourceFromFile(filePath);
    var tokens = getTokensFromString(exampleString);
    var methodDescription = BSLDescriptionReader.parseMethodDescription(tokens);

    assertThat(methodDescription).isNotNull();
    assertThat(methodDescription.getDescription()).isEqualTo(exampleString);
    assertThat(methodDescription.getPurposeDescription())
      .isEqualTo("Инициализирует структуру параметров для взаимодействия с файловой системой.");
    assertThat(methodDescription.getCallOptions()).isEmpty();
    assertThat(methodDescription.getDeprecationInfo()).isEmpty();
    assertThat(methodDescription.getExamples()).isEmpty();
    assertThat(methodDescription.getLink()).isEmpty();
    assertThat(methodDescription.getParameters()).hasSize(1);
    checkParameter(methodDescription.getParameters().get(0),
      "РежимДиалога", 1, "", false);
    checkType(methodDescription.getParameters().get(0).getTypes().get(0),
      "РежимДиалогаВыбораФайла", "режим работы конструируемого диалога выбора файлов.",
      0, "", false);

    assertThat(
      Objects.equals(methodDescription.getSimpleRange(), create(7, 2)))
      .isTrue();
    assertThat(methodDescription.getReturnedValue()).hasSize(1);
    checkType(methodDescription.getReturnedValue().get(0),
      "см. ФайловаяСистемаКлиент.ПараметрыЗагрузкиФайла", "",
      0, "ФайловаяСистемаКлиент.ПараметрыЗагрузкиФайла", true);
  }

  @Test
  void parseMethodDescription3() {
    var filePath = "src/test/resources/methodDescription/example3.bsl";
    var exampleString = TestUtils.getSourceFromFile(filePath);
    var tokens = getTokensFromString(exampleString);
    var methodDescription = BSLDescriptionReader.parseMethodDescription(tokens);

    assertThat(methodDescription).isNotNull();
    assertThat(methodDescription.getDescription()).isEqualTo(exampleString);
    assertThat(methodDescription.getPurposeDescription()).isEqualTo("Загружает настройку из хранилища общих настроек, как метод платформы Загрузить,\n" +
      "объектов СтандартноеХранилищеНастроекМенеджер или ХранилищеНастроекМенеджер.<Имя хранилища>,\n" +
      "но с поддержкой длины ключа настроек более 128 символов путем хеширования части,\n" +
      "которая превышает 96 символов.\n" +
      "Кроме того, возвращает указанное значение по умолчанию, если настройки не существуют.\n" +
      "Если нет права СохранениеДанныхПользователя, возвращается значение по умолчанию без ошибки.\n" +
      "\n" +
      "В возвращаемом значении очищаются ссылки на несуществующий объект в базе данных, а именно\n" +
      "- возвращаемая ссылка заменяется на указанное значение по умолчанию;\n" +
      "- из данных типа Массив ссылки удаляются;\n" +
      "- у данных типа Структура и Соответствие ключ не меняется, а значение устанавливается Неопределено;\n" +
      "- анализ значений в данных типа Массив, Структура, Соответствие выполняется рекурсивно.");
    assertThat(methodDescription.getCallOptions()).isEmpty();
    assertThat(methodDescription.getDeprecationInfo()).isEmpty();
    assertThat(methodDescription.getExamples()).isEmpty();
    assertThat(methodDescription.getLink()).isEmpty();
    assertThat(methodDescription.getParameters()).hasSize(5);
    checkParameter(methodDescription.getParameters().get(0),
      "КлючОбъекта", 1, "", false);
    checkType(methodDescription.getParameters().get(0).getTypes().get(0),
      "Строка",
      "см. синтакс-помощник платформы.", 0, "", false);
    checkParameter(methodDescription.getParameters().get(1), "КлючНастроек", 1, "", false);
    checkType(methodDescription.getParameters().get(1).getTypes().get(0),
      "Строка", "см. синтакс-помощник платформы.", 0, "", false);
    checkParameter(methodDescription.getParameters().get(2), "ЗначениеПоУмолчанию", 1, "", false);
    checkType(methodDescription.getParameters().get(2).getTypes().get(0),
      "Произвольный", "значение, которое возвращается, если настройки не существуют.\n" +
        "Если не указано, возвращается значение Неопределено.",
      0, "", false);
    checkParameter(methodDescription.getParameters().get(3), "ОписаниеНастроек", 1, "", false);
    checkParameter(methodDescription.getParameters().get(4), "ИмяПользователя", 1, "", false);
    checkType(methodDescription.getParameters().get(4).getTypes().get(0),
      "Строка", "см. синтакс-помощник платформы.", 0, "", false);

    assertThat(
      Objects.equals(methodDescription.getSimpleRange(), create(23, 2)))
      .isTrue();
    assertThat(methodDescription.getReturnedValue()).hasSize(1);
    checkType(methodDescription.getReturnedValue().get(0),
      "Произвольный", "см. синтакс-помощник платформы.", 0, "", false);
  }

  @Test
  void parseMethodDescription4() {
    var filePath = "src/test/resources/methodDescription/example4.bsl";
    var exampleString = TestUtils.getSourceFromFile(filePath);
    var tokens = getTokensFromString(exampleString);
    var methodDescription = BSLDescriptionReader.parseMethodDescription(tokens);

    assertThat(methodDescription).isNotNull();
    assertThat(methodDescription.getDescription()).isEqualTo(exampleString);
    assertThat(methodDescription.getPurposeDescription()).isEqualTo("Описание функции.\n" +
      "Многострочное.");
    assertThat(methodDescription.getCallOptions()).isEmpty();
    assertThat(methodDescription.getDeprecationInfo()).isEmpty();
    assertThat(methodDescription.getExamples()).isEmpty();
    assertThat(methodDescription.getLink()).isEmpty();
    assertThat(methodDescription.getParameters()).hasSize(3);
    checkParameter(methodDescription.getParameters().get(0),
      "П1", 2, "", false);
    checkType(methodDescription.getParameters().get(0).getTypes().get(0),
      "Дата", "Описание даты/числа", 0, "", false);
    checkType(methodDescription.getParameters().get(0).getTypes().get(1),
      "Число", "Описание даты/числа", 0, "", false);
    checkParameter(methodDescription.getParameters().get(1), "П2", 1, "", false);
    checkType(methodDescription.getParameters().get(1).getTypes().get(0),
      "Число", "Описание числа", 0, "", false);
    checkParameter(methodDescription.getParameters().get(2), "П3", 1, "", false);
    checkType(methodDescription.getParameters().get(2).getTypes().get(0),
      "Строка", "Описание строки", 0, "", false);

    assertThat(
      Objects.equals(methodDescription.getSimpleRange(), create(10, 2)))
      .isTrue();
    assertThat(methodDescription.getReturnedValue()).hasSize(1);
    checkType(methodDescription.getReturnedValue().get(0),
      "Строка", "вернувшаяся строка", 0, "", false);
  }

  @Test
  void parseMethodDescription5() {
    var filePath = "src/test/resources/methodDescription/example7.bsl";
    var exampleString = TestUtils.getSourceFromFile(filePath);
    var tokens = getTokensFromString(exampleString);
    var methodDescription = BSLDescriptionReader.parseMethodDescription(tokens);

    assertThat(methodDescription).isNotNull();
    assertThat(methodDescription.getDescription()).isEqualTo(exampleString);
    assertThat(methodDescription.getPurposeDescription()).contains("Copyright (c) 2020, ООО 1С-Софт");
    assertThat(methodDescription.getCallOptions()).isEmpty();
    assertThat(methodDescription.getDeprecationInfo()).isEmpty();
    assertThat(methodDescription.getExamples()).isEmpty();
    assertThat(methodDescription.getLink()).isEmpty();
    assertThat(methodDescription.getParameters()).isEmpty();

    assertThat(
      Objects.equals(methodDescription.getSimpleRange(), create(329, 2)))
      .isTrue();
    assertThat(methodDescription.getReturnedValue()).hasSize(1);
    checkType(methodDescription.getReturnedValue().get(0),
      "Структура", "настройки (дополнительные свойства) отчета, хранящиеся в данных формы:",
      10, "", false);

    checkParameter(methodDescription.getReturnedValue().get(0).getParameters().get(0),
      "ФормироватьСразу", 1, "", false);
    checkType(methodDescription.getReturnedValue().get(0).getParameters().get(0).getTypes().get(0),
      "Булево", "значение по умолчанию для флажка \"Формировать сразу\".\n" +
        "Когда флажок включен, то отчет будет формироваться после открытия,\n" +
        "после выбора пользовательских настроек, после выбора другого варианта отчета.",
      0, "", false);

    checkParameter(methodDescription.getReturnedValue().get(0).getParameters().get(1),
      "ВыводитьСуммуВыделенныхЯчеек", 1, "", false);
    checkType(methodDescription.getReturnedValue().get(0).getParameters().get(1).getTypes().get(0),
      "Булево", "если Истина, то в отчете будет выводиться поле автосуммы.",
      0, "", false);

    checkParameter(methodDescription.getReturnedValue().get(0).getParameters().get(4),
      "РазрешеноВыбиратьИНастраиватьВариантыБезСохранения", 1, "", false);
    checkType(methodDescription.getReturnedValue().get(0).getParameters().get(4).getTypes().get(0),
      "Булево", "если Истина,\n" +
        "то есть возможность выбора и настройки предопределенных вариантов отчета, но без возможности сохранения\n" +
        "выполненных настроек. Например, может быть задано для контекстных отчетов (открываемых с параметрами),\n" +
        "у которых есть несколько вариантов.",
      0, "", false);

    checkParameter(methodDescription.getReturnedValue().get(0).getParameters().get(5),
      "ПараметрыРасположенияЭлементовУправления", 2, "", false);
    checkType(methodDescription.getReturnedValue().get(0).getParameters().get(5).getTypes().get(1),
      "Неопределено", "варианты:\n" +
        "Неопределено - параметры элементов управления общей формы отчетов \"по умолчанию\".\n" +
        "Структура - с именами настройки в коллекции НастройкиКомпоновкиДанных свойства Настройки\n" +
        "типа КомпоновщикНастроекКомпоновкиДанных:",
      2, "", false);
  }

  @Test
  void parseMethodDescription6() {
    var filePath = "src/test/resources/methodDescription/example8.bsl";
    var exampleString = TestUtils.getSourceFromFile(filePath);
    var tokens = getTokensFromString(exampleString);
    var methodDescription = BSLDescriptionReader.parseMethodDescription(tokens);

    assertThat(methodDescription).isNotNull();
    assertThat(methodDescription.getDescription()).isEqualTo(exampleString);
    assertThat(methodDescription.getPurposeDescription()).contains("Copyright (c) 2020, ООО 1С-Софт");
    assertThat(methodDescription.getCallOptions()).isEmpty();
    assertThat(methodDescription.getDeprecationInfo()).isEmpty();
    assertThat(methodDescription.getExamples()).isEmpty();
    assertThat(methodDescription.getLink()).isEmpty();

    assertThat(
      Objects.equals(methodDescription.getSimpleRange(), create(329, 2)))
      .isTrue();
    assertThat(methodDescription.getReturnedValue()).isEmpty();
    assertThat(methodDescription.getParameters()).hasSize(1);
    var firstParameter = methodDescription.getParameters().get(0);
    checkParameter(firstParameter,
      "Входной", 1, "", false);

    checkType(firstParameter.getTypes().get(0),
      "Структура", "настройки (дополнительные свойства) отчета, хранящиеся в данных формы:",
      10, "", false);

    checkParameter(firstParameter.getTypes().get(0).getParameters().get(0),
      "ФормироватьСразу", 1, "", false);
    checkType(firstParameter.getTypes().get(0).getParameters().get(0).getTypes().get(0),
      "Булево", "значение по умолчанию для флажка \"Формировать сразу\".\n" +
        "Когда флажок включен, то отчет будет формироваться после открытия,\n" +
        "после выбора пользовательских настроек, после выбора другого варианта отчета.",
      0, "", false);

    checkParameter(firstParameter.getTypes().get(0).getParameters().get(1),
      "ВыводитьСуммуВыделенныхЯчеек", 1, "", false);
    checkType(firstParameter.getTypes().get(0).getParameters().get(1).getTypes().get(0),
      "Булево", "если Истина, то в отчете будет выводиться поле автосуммы.",
      0, "", false);

    checkParameter(firstParameter.getTypes().get(0).getParameters().get(4),
      "РазрешеноВыбиратьИНастраиватьВариантыБезСохранения", 1, "", false);
    checkType(firstParameter.getTypes().get(0).getParameters().get(4).getTypes().get(0),
      "Булево", "если Истина,\n" +
        "то есть возможность выбора и настройки предопределенных вариантов отчета, но без возможности сохранения\n" +
        "выполненных настроек. Например, может быть задано для контекстных отчетов (открываемых с параметрами),\n" +
        "у которых есть несколько вариантов.",
      0, "", false);

    checkParameter(firstParameter.getTypes().get(0).getParameters().get(5),
      "ПараметрыРасположенияЭлементовУправления", 2, "", false);
    checkType(firstParameter.getTypes().get(0).getParameters().get(5).getTypes().get(1),
      "Неопределено", "варианты:\n" +
        "Неопределено - параметры элементов управления общей формы отчетов \"по умолчанию\".\n" +
        "Структура - с именами настройки в коллекции НастройкиКомпоновкиДанных свойства Настройки\n" +
        "типа КомпоновщикНастроекКомпоновкиДанных:",
      2, "", false);
  }

  @Test
  void parseMethodDescription7() {
    var exampleString = "// Параметры: \n// See CommonModule.MyModule.MyFunc()";
    var tokens = getTokensFromString(exampleString);
    var methodDescription = BSLDescriptionReader.parseMethodDescription(tokens);

    assertThat(methodDescription).isNotNull();
    assertThat(methodDescription.getDescription()).isEqualTo(exampleString);
    assertThat(methodDescription.getPurposeDescription()).isEmpty();
    assertThat(methodDescription.getCallOptions()).isEmpty();
    assertThat(methodDescription.getDeprecationInfo()).isEmpty();
    assertThat(methodDescription.getExamples()).isEmpty();
    assertThat(methodDescription.getLink()).isEmpty();
    assertThat(methodDescription.getParameters()).hasSize(1);
    checkParameter(methodDescription.getParameters().get(0),
      "", 0, "CommonModule.MyModule.MyFunc()", true);
    assertThat(methodDescription.getReturnedValue()).isEmpty();
  }

  @Test
  void parseMethodDescription8() {
    var exampleString = "// See CommonModule.MyModule.MyFunc()";
    var tokens = getTokensFromString(exampleString);
    var methodDescription = BSLDescriptionReader.parseMethodDescription(tokens);

    assertThat(methodDescription).isNotNull();
    assertThat(methodDescription.getDescription()).isEqualTo(exampleString);
    assertThat(methodDescription.getPurposeDescription()).isEqualTo("See CommonModule.MyModule.MyFunc()");
    assertThat(methodDescription.getCallOptions()).isEmpty();
    assertThat(methodDescription.getDeprecationInfo()).isEmpty();
    assertThat(methodDescription.getExamples()).isEmpty();
    assertThat(methodDescription.getLink()).isEqualTo("CommonModule.MyModule.MyFunc()");
    assertThat(methodDescription.getParameters()).isEmpty();
    assertThat(methodDescription.getReturnedValue()).isEmpty();
  }

  @Test
  void parseVariableDescription() {
    var exampleString = "// Описание переменной";
    var tokens = getTokensFromString(exampleString);
    var variableDescription = BSLDescriptionReader.parseVariableDescription(tokens);

    assertThat(variableDescription).isNotNull();
    assertThat(variableDescription.getDescription()).isEqualTo(exampleString);
    assertThat(variableDescription.getPurposeDescription()).contains("Описание переменной");
    assertThat(variableDescription.getTrailingDescription()).isNotPresent();
    assertThat(variableDescription.getDeprecationInfo()).isEmpty();
    assertThat(variableDescription.isDeprecated()).isFalse();
    assertThat(variableDescription.getLink()).isEmpty();

    assertThat(
      Objects.equals(variableDescription.getSimpleRange(), create(0, 22)))
      .isTrue();
  }

  @Test
  void parseVariableDescription2() {
    var exampleString = "// Описание переменной";
    var exampleString2 = "// Висячее Описание переменной";
    var tokens = getTokensFromString(exampleString);
    var token = getTokensFromString(exampleString2).get(0);
    var variableDescription = BSLDescriptionReader.parseVariableDescription(tokens, token);

    assertThat(variableDescription).isNotNull();
    assertThat(variableDescription.getDescription()).isEqualTo(exampleString);
    assertThat(variableDescription.getPurposeDescription()).contains("Описание переменной");
    assertThat(variableDescription.getTrailingDescription()).isPresent();
    assertThat(variableDescription.getDeprecationInfo()).isEmpty();
    assertThat(variableDescription.isDeprecated()).isFalse();
    assertThat(variableDescription.getLink()).isEmpty();

    assertThat(
      Objects.equals(variableDescription.getSimpleRange(), create(0, 22)))
      .isTrue();

    assertThat(variableDescription.getTrailingDescription().get().getDescription()).isEqualTo(exampleString2);
    assertThat(variableDescription.getTrailingDescription().get().getPurposeDescription())
      .contains("Висячее Описание переменной");
    assertThat(variableDescription.getTrailingDescription().get().getTrailingDescription()).isNotPresent();
    assertThat(variableDescription.getTrailingDescription().get().getDeprecationInfo()).isEmpty();
    assertThat(variableDescription.getTrailingDescription().get().isDeprecated()).isFalse();
    assertThat(variableDescription.getTrailingDescription().get().getLink()).isEmpty();
  }

  @Test
  void parseVariableDescription3() {
    var exampleString = "// Описание переменной\n// Устарела. см. НоваяПеременная";
    var tokens = getTokensFromString(exampleString);
    var variableDescription = BSLDescriptionReader.parseVariableDescription(tokens);

    assertThat(variableDescription).isNotNull();
    assertThat(variableDescription.getDescription()).isEqualTo(exampleString);
    assertThat(variableDescription.getPurposeDescription()).contains("Описание переменной");
    assertThat(variableDescription.getTrailingDescription()).isNotPresent();
    assertThat(variableDescription.getDeprecationInfo()).isEqualTo("см. НоваяПеременная");
    assertThat(variableDescription.isDeprecated()).isTrue();
    assertThat(variableDescription.getLink()).isEmpty();

    assertThat(
      Objects.equals(variableDescription.getSimpleRange(), create(1, 32)))
      .isTrue();
  }

  @Test
  void parseVariableDescription4() {
    var exampleString = "// см. НоваяПеременная";
    var tokens = getTokensFromString(exampleString);
    var variableDescription = BSLDescriptionReader.parseVariableDescription(tokens);

    assertThat(variableDescription).isNotNull();
    assertThat(variableDescription.getDescription()).isEqualTo(exampleString);
    assertThat(variableDescription.getPurposeDescription()).contains("см. НоваяПеременная");
    assertThat(variableDescription.getTrailingDescription()).isNotPresent();
    assertThat(variableDescription.getDeprecationInfo()).isEmpty();
    assertThat(variableDescription.isDeprecated()).isFalse();
    assertThat(variableDescription.getLink()).isEqualTo("НоваяПеременная");

    assertThat(
      Objects.equals(variableDescription.getSimpleRange(), create(0, 22)))
      .isTrue();
  }

  private List<Token> getTokensFromString(String exampleString) {
    var tokenizer = new BSLTokenizer(exampleString);
    return tokenizer.getTokens().stream()
      .filter(token -> token.getType() == BSLParser.LINE_COMMENT)
      .collect(Collectors.toList());
  }

  private void checkParameter(ParameterDescription parameter,
                              String name,
                              int countTypes,
                              String link,
                              boolean isHyperlink) {
    assertThat(parameter.getName()).isEqualTo(name);
    assertThat(parameter.getLink()).isEqualTo(link);
    assertThat(parameter.getTypes()).hasSize(countTypes);
    assertThat(parameter.isHyperlink()).isEqualTo(isHyperlink);
  }

  private void checkType(TypeDescription type,
                         String name,
                         String description,
                         int countParameters,
                         String link,
                         boolean isHyperlink) {
    assertThat(type.getName()).isEqualTo(name);
    assertThat(type.getDescription()).isEqualTo(description);
    assertThat(type.getLink()).isEqualTo(link);
    assertThat(type.getParameters()).hasSize(countParameters);
    assertThat(type.isHyperlink()).isEqualTo(isHyperlink);
  }

  private SimpleRange create(int endLine, int endChar) {
    return new SimpleRange(0, 0, endLine, endChar);
  }
}