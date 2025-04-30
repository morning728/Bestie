package com.morning.taskapimain.service.report;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.morning.taskapimain.entity.dto.ProjectDTO;
import com.morning.taskapimain.entity.dto.TaskDTO;
import com.morning.taskapimain.entity.dto.UserWithRoleDTO;
import com.morning.taskapimain.entity.project.Permission;
import com.morning.taskapimain.entity.project.ProjectRole;
import com.morning.taskapimain.entity.project.ProjectStatus;
import com.morning.taskapimain.entity.project.ProjectTag;
import com.morning.taskapimain.entity.task.Task;
import com.morning.taskapimain.entity.task.TaskAssignee;
import com.morning.taskapimain.entity.user.Contacts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportBuilder {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final DeviceRgb mainColor = new DeviceRgb(153, 50, 204); // #9932CC

    public static DeviceRgb hexToRgb(String hex) {
        hex = hex.replace("#", ""); // Убираем #
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return new DeviceRgb(r, g, b);
    }

    public byte[] buildReport(ProjectDTO project, List<TaskDTO> tasks, List<Contacts> contacts) throws Exception {
        // Сбор данных
        List<ProjectRole> roles = project.getRoles();
        List<ProjectTag> tags = project.getTags();
        List<ProjectStatus> statuses = project.getStatuses();
        List<UserWithRoleDTO> members = project.getMembers();
        // Группировка
        Map<String, Long> tasksByStatus = tasks.stream().collect(Collectors.groupingBy(
                t -> project.getStatuses().stream().filter(s -> s.getId().equals(t.getStatusId())).findFirst().map(ProjectStatus::getName).orElse("Без статуса"),
                Collectors.counting()
        ));

        Map<String, Long> tasksByAssignee = tasks.stream()
                .flatMap(task -> task.getAssignees().stream().map(a -> a.getUserId()))
                .collect(Collectors.groupingBy(
                        uid -> project.getMembers().stream()
                                .filter(m -> m.getUserId().equals(uid.toString()))
                                .findFirst().map(UserWithRoleDTO::getUsername).orElse("Неизвестный"),
                        Collectors.counting()
                ));

        Map<String, Long> tasksByTag = tasks.stream()
                .flatMap(t -> t.getTags().stream())
                .collect(Collectors.groupingBy(ProjectTag::getName, Collectors.counting()));


        // Создание отчета
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);
        doc.setMargins(50, 36, 50, 36); // верх, право, низ, лево


        PdfFont font = PdfFontFactory.createFont("fonts/DejaVuSans.ttf", PdfEncodings.IDENTITY_H);


        createIntroPage(doc, project, font);

        createStatusBlock(doc, statuses, tasksByStatus, font);

        createTagBlock(doc, tags, tasksByTag, font);

        createMembersBlock(doc, project, contacts, font);

        createRolesBlock(doc, roles, font);

        createAdvancedTaskAnalyticsBlock(doc, tasks, members, font);

        createAssigneeBlock(doc, project, tasks, font);


        doc.close();
        return baos.toByteArray();
    }

    private void createCoverPage(Document doc, ProjectDTO project, PdfFont font) {
        DeviceRgb color = hexToRgb(project.getColor() != null ? project.getColor() : "#9932CC");


        Paragraph title = new Paragraph("Отчет по проекту: " + project.getTitle())
                .setFont(font)
                .setFontSize(24)
                .setBold()
                .setFontColor(color)
                .setTextAlignment(TextAlignment.CENTER);

        Paragraph status = new Paragraph("Статус: " + project.getStatus())
                .setFont(font)
                .setFontSize(12)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER);

        Paragraph date = new Paragraph("Дата создания отчета: " + java.time.LocalDate.now().format(dateFormatter))
                .setFont(font)
                .setFontSize(10)
                .setFontColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER);

        doc.add(title);
        doc.add(status);
        doc.add(date);
        doc.add(new AreaBreak());
    }

    private void createTableOfContents(Document doc, PdfFont font) {
        doc.add(new Paragraph("Содержание")
                .setFont(font)
                .setFontSize(16)
                .setBold()
                .setFontColor(mainColor));

        List<String> sections = List.of(
                "Введение",
                "Задачи по статусам",
                "Использование тегов",
                "Участники проекта",
                "Роли проекта и их права",
                "Расширенная аналитика по задачам",
                "Распределение задач по участникам"
        );

        for (int i = 0; i < sections.size(); i++) {
            doc.add(new Paragraph((i + 1) + ". " + sections.get(i))
                    .setFont(font)
                    .setFontSize(11)
                    .setMarginBottom(5));
        }

        doc.add(new AreaBreak());
    }

    private void createStatusBlock(Document doc, List<ProjectStatus> statuses, Map<String, Long> tasksByStatus, PdfFont font) throws Exception {
        doc.add(new AreaBreak());
        doc.add(new Paragraph("\n Задачи по статусам:").setFont(font).setFontColor(mainColor));

        Table tableStatus = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();
        tableStatus.setFont(font);

// Заголовки
        tableStatus.addCell(new Cell().add(new Paragraph("Название").setFont(font).setFontColor(ColorConstants.GRAY)));
        tableStatus.addCell(new Cell().add(new Paragraph("Цвет").setFont(font).setFontColor(ColorConstants.GRAY)));
        tableStatus.addCell(new Cell().add(new Paragraph("Кол-во задач").setFont(font).setFontColor(ColorConstants.GRAY)));

// Данные
        statuses.forEach((status) -> {

            // Название статуса
            tableStatus.addCell(new Cell().add(new Paragraph(status.getName()).setFont(font)));

            // Цвет статуса
            if (status.getColor() != null) {
                String colorHex = status.getColor();
                Cell colorCell = new Cell()
                        .add(new Paragraph(colorHex)
                                .setFont(font)
                                .setFontColor(hexToRgb(colorHex))); // здесь задаём цвет только тексту!
                tableStatus.addCell(colorCell);
            } else {
                tableStatus.addCell(new Cell().add(new Paragraph("-").setFont(font)));
            }

            // Количество задач
            tableStatus.addCell(new Cell()
                    .add(new Paragraph(tasksByStatus.get(status.getName()) != null ? tasksByStatus.get(status.getName()).toString() : "0")
                            .setFont(font)));
        });

        doc.add(tableStatus);
        doc.add(new Paragraph("\n"));

        Map<String, String> statusColors = statuses.stream()
                .collect(Collectors.toMap(ProjectStatus::getName, ProjectStatus::getColor));

        doc.add(new Image(ImageDataFactory.create(
                createPieChartWithColors(tasksByStatus, "Распределение по статусам", statusColors)
        )).setHorizontalAlignment(HorizontalAlignment.CENTER).setAutoScale(true));

    }

    private void createTagBlock(Document doc, List<ProjectTag> tags, Map<String, Long> tasksByTag, PdfFont font) throws Exception {
        doc.add(new AreaBreak());
        doc.add(new Paragraph("\n Использование тегов:").setFont(font).setFontColor(mainColor));

        Table tableTag = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 3})).useAllAvailableWidth();
        tableTag.setFont(font);

        // Заголовки
        tableTag.addCell(new Cell().add(new Paragraph("Тег").setFont(font).setFontColor(ColorConstants.GRAY)));
        tableTag.addCell(new Cell().add(new Paragraph("Цвет").setFont(font).setFontColor(ColorConstants.GRAY)));
        tableTag.addCell(new Cell().add(new Paragraph("Упоминаний").setFont(font).setFontColor(ColorConstants.GRAY)));
        tableTag.addCell(new Cell().add(new Paragraph("Доля").setFont(font).setFontColor(ColorConstants.GRAY)));

        long totalMentions = tasksByTag.values().stream().mapToLong(Long::longValue).sum();

        tags.forEach(tag -> {
            String tagName = tag.getName();
            long count = tasksByTag.getOrDefault(tagName, 0L);
            String percent = totalMentions > 0 ? String.format("%.1f%%", count * 100.0 / totalMentions) : "0%";

            // Название
            tableTag.addCell(new Cell().add(new Paragraph(tagName).setFont(font)));

            // Цвет
            if (tag.getColor() != null) {
                tableTag.addCell(new Cell().add(new Paragraph(tag.getColor())
                        .setFont(font)
                        .setFontColor(hexToRgb(tag.getColor()))));
            } else {
                tableTag.addCell(new Cell().add(new Paragraph("-").setFont(font)));
            }

            // Кол-во
            tableTag.addCell(new Cell().add(new Paragraph(String.valueOf(count)).setFont(font)));

            // Процент
            tableTag.addCell(new Cell().add(new Paragraph(percent).setFont(font)));
        });

        doc.add(tableTag);
        doc.add(new Paragraph("\n"));

        // Цвета
        Map<String, String> tagColors = tags.stream()
                .collect(Collectors.toMap(ProjectTag::getName, ProjectTag::getColor));

        // Диаграмма
        doc.add(new Image(ImageDataFactory.create(
                createPieChartWithColors(tasksByTag, "Распределение по тегам", tagColors)
        )).setHorizontalAlignment(HorizontalAlignment.CENTER).setAutoScale(true));
    }

    private void createAssigneeBlock(Document doc, ProjectDTO project, List<TaskDTO> tasks, PdfFont font) throws Exception {
        doc.add(new AreaBreak());
        doc.add(new Paragraph("\n Распределение задач по участникам:").setFont(font).setFontColor(mainColor));

        List<UserWithRoleDTO> members = project.getMembers();

        // Собираем мапу userId → количество задач
        Map<Long, Long> taskCountByUserId = tasks.stream()
                .flatMap(t -> t.getAssignees().stream().map(TaskAssignee::getUserId))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // Таблица
        Table table = new Table(UnitValue.createPercentArray(4)).useAllAvailableWidth();
        table.setFont(font);
        table.addCell(new Cell().add(new Paragraph("Имя пользователя").setFont(font).setFontColor(ColorConstants.GRAY)));
        table.addCell(new Cell().add(new Paragraph("Роль").setFont(font).setFontColor(ColorConstants.GRAY)));
        table.addCell(new Cell().add(new Paragraph("ID").setFont(font).setFontColor(ColorConstants.GRAY)));
        table.addCell(new Cell().add(new Paragraph("Кол-во задач").setFont(font).setFontColor(ColorConstants.GRAY)));

        Map<String, Long> tasksByUsername = new LinkedHashMap<>();

        members.forEach(member -> {
            String fullName = member.getFirstName() + " " + member.getLastName();
            String username = fullName.trim().isBlank() ? member.getUsername() : fullName.trim();
            Long userId = member.getUserId();
            Long count = taskCountByUserId.getOrDefault(userId, 0L);
            tasksByUsername.put(username, count);

            table.addCell(new Cell().add(new Paragraph(username).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(member.getRoleName() != null ? member.getRoleName() : "—").setFont(font)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(userId)).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(count.toString()).setFont(font)));
        });

        doc.add(table);
        doc.add(new Paragraph("\n"));

        // Вставка диаграммы
        doc.add(new Image(ImageDataFactory.create(
                createBarChart(tasksByUsername, "Нагрузка по участникам", "Пользователь", "Количество задач")
        )).setHorizontalAlignment(HorizontalAlignment.CENTER).setAutoScale(true));
    }

    private void createMembersBlock(Document doc, ProjectDTO project, List<Contacts> contacts, PdfFont font) {
        doc.add(new AreaBreak());
        doc.add(new Paragraph("\n Участники проекта:").setFont(font).setFontColor(mainColor));

        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 3, 3, 3, 2})).useAllAvailableWidth();
        table.setFont(font);

        // Заголовки таблицы
        Stream.of("Имя пользователя", "ФИО", "Электронная почта", "Telegram ID", "Роль")
                .forEach(header -> table.addCell(
                        new Cell().add(new Paragraph(header).setFont(font).setFontColor(ColorConstants.GRAY)))
                );

        for (UserWithRoleDTO member : project.getMembers()) {
            String fullName = Stream.of(member.getFirstName(), member.getLastName())
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(" ")).trim();

            Contacts contact = contacts.stream()
                    .filter(c -> c.getUsername().equals(member.getUsername()))
                    .findFirst()
                    .orElse(null);

            table.addCell(new Cell().add(new Paragraph(member.getUsername()).setFont(font)));
            table.addCell(new Cell().add(new Paragraph(!fullName.isBlank() ? fullName : "—").setFont(font)));
            table.addCell(new Cell().add(new Paragraph(
                    contact != null && contact.getEmail() != null ? contact.getEmail() : "—").setFont(font)));
            table.addCell(new Cell().add(new Paragraph(
                    contact != null && contact.getTelegramId() != null ? contact.getTelegramId() : "—").setFont(font)));
            table.addCell(new Cell().add(new Paragraph(
                    member.getRoleName() != null ? member.getRoleName() : "—").setFont(font)));
        }

        doc.add(table);
        doc.add(new Paragraph("\n"));
    }

    private void createRolesBlock(Document doc, List<ProjectRole> roles, PdfFont font) {
        doc.add(new AreaBreak());
        doc.add(new Paragraph("\n Роли проекта и их права доступа:").setFont(font).setFontColor(mainColor));

        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 7})).useAllAvailableWidth();
        table.setFont(font);

        table.addCell(new Cell().add(new Paragraph("Роль").setFont(font).setFontColor(ColorConstants.GRAY)));
        table.addCell(new Cell().add(new Paragraph("Права доступа").setFont(font).setFontColor(ColorConstants.GRAY)));

        for (ProjectRole role : roles) {
            role.deserializePermissions();

            table.addCell(new Cell().add(new Paragraph(role.getName()).setFont(font)));

            StringBuilder permissionsText = new StringBuilder();

            for (Permission perm : Permission.values()) {
                if (perm.equals(Permission.CAN_COMMENT_TASKS)) continue;
                boolean hasPermission = role.getPermissionsJson() != null && Boolean.TRUE.equals(role.getPermissionsJson().get(perm));
                String label = formatPermissionName(perm.name());
                permissionsText.append(hasPermission ? "✔️ " : "ⅹ ").append(label).append("\n");
            }

            table.addCell(new Cell().add(new Paragraph(permissionsText.toString()).setFont(font)));
        }

        doc.add(table);
        doc.add(new Paragraph("\n"));
    }

    private void createAdvancedTaskAnalyticsBlock(Document doc, List<TaskDTO> tasks, List<UserWithRoleDTO> members, PdfFont font) {
        doc.add(new AreaBreak());
        doc.add(new Paragraph(" Расширенная аналитика по задачам").setFont(font).setFontColor(mainColor));

        long totalTasks = tasks.size();

        // Средняя продолжительность задачи (в днях)
        double avgDuration = tasks.stream()
                .filter(t -> t.getStartDate() != null && t.getEndDate() != null)
                .mapToLong(t -> ChronoUnit.DAYS.between(t.getStartDate(), t.getEndDate()))
                .average().orElse(0.0);

        // Средняя длина описания
        double avgDescriptionLength = tasks.stream()
                .mapToInt(t -> t.getDescription() != null ? t.getDescription().length() : 0)
                .average().orElse(0.0);

        // Среднее количество исполнителей
        double avgAssignees = tasks.stream()
                .mapToInt(t -> t.getAssignees() != null ? t.getAssignees().size() : 0)
                .average().orElse(0.0);

        // Автор, создавший больше всего задач
        Map<Long, Long> tasksByCreator = tasks.stream()
                .collect(Collectors.groupingBy(TaskDTO::getCreatedBy, Collectors.counting()));
        Long topCreatorId = tasksByCreator.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);
        String topCreator = members.stream()
                .filter(m -> m.getUserId().equals(topCreatorId))
                .map(UserWithRoleDTO::getUsername)
                .findFirst().orElse("Неизвестный");

        // Самый активный пользователь по времени задач
        Map<Long, Long> timePerUser = new HashMap<>();
        for (TaskDTO t : tasks) {
            if (t.getStartDate() != null && t.getEndDate() != null && !t.getAssignees().stream().map(TaskAssignee::getUserId).toList().isEmpty()) {
                long days = ChronoUnit.DAYS.between(t.getStartDate(), t.getEndDate());
                for (Long uid : t.getAssignees().stream().map(TaskAssignee::getUserId).toList()) {
                    timePerUser.put(uid, timePerUser.getOrDefault(uid, 0L) + days);
                }
            }
        }
        Long topPerformerId = timePerUser.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);
        String topPerformer = members.stream()
                .filter(m -> m.getUserId().equals(topPerformerId))
                .map(UserWithRoleDTO::getUsername)
                .findFirst().orElse("Неизвестный");

        // Количество архивированных задач
        double archivedCount = tasks.stream().filter(t -> Boolean.TRUE.equals(t.getIsArchived())).count();

        // Количество задач без описания
        long withoutDescription = tasks.stream().filter(t -> t.getDescription() == null || t.getDescription().isBlank()).count();

        // Количество задач с более чем 1 тег
        long multiTagTasks = tasks.stream().filter(t -> t.getTags() != null && t.getTags().size() > 1).count();

        // Формируем таблицу
        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.setFont(font);

        table.addCell(new Cell().add(new Paragraph("Всего задач").setFont(font)));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(totalTasks)).setFont(font)));

        table.addCell(new Cell().add(new Paragraph("Средняя продолжительность (дней)").setFont(font)));
        table.addCell(new Cell().add(new Paragraph(String.format("%.2f", avgDuration)).setFont(font)));

        table.addCell(new Cell().add(new Paragraph("Средняя длина описания").setFont(font)));
        table.addCell(new Cell().add(new Paragraph(String.format("%.2f символов", avgDescriptionLength)).setFont(font)));

        table.addCell(new Cell().add(new Paragraph("Среднее количество исполнителей").setFont(font)));
        table.addCell(new Cell().add(new Paragraph(String.format("%.2f", avgAssignees)).setFont(font)));

        table.addCell(new Cell().add(new Paragraph("Автор наибольшего числа задач").setFont(font)));
        table.addCell(new Cell().add(new Paragraph(topCreator).setFont(font)));

        table.addCell(new Cell().add(new Paragraph("Самый загруженный исполнитель").setFont(font)));
        table.addCell(new Cell().add(new Paragraph(topPerformer).setFont(font)));

        table.addCell(new Cell().add(new Paragraph("Задач без описания").setFont(font)));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(withoutDescription)).setFont(font)));

        table.addCell(new Cell().add(new Paragraph("Задач с >1 тегом").setFont(font)));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(multiTagTasks)).setFont(font)));

        table.addCell(new Cell().add(new Paragraph("Кол-во архивированных задач").setFont(font)));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(archivedCount)).setFont(font)));

        doc.add(table);
    }

    private void createIntroPage(Document doc, ProjectDTO project, PdfFont font) {
        DeviceRgb color = hexToRgb(project.getColor() != null ? project.getColor() : "#9932CC");

        // Заголовок
        Paragraph title = new Paragraph("Проект: " + project.getTitle())
                .setFont(font)
                .setFontSize(22)
                .setFontColor(color)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        doc.add(title);

        // Подзаголовок
        Paragraph subtitle = new Paragraph("Аналитический отчет от " + java.time.LocalDate.now().format(dateFormatter))
                .setFont(font)
                .setFontSize(14)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(30);
        doc.add(subtitle);

        // Основная таблица характеристик
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{35, 65})).useAllAvailableWidth();
        infoTable.setFont(font);
        infoTable.addCell(createHeaderCell("Описание"));
        infoTable.addCell(createBodyCell(project.getDescription() != null ? project.getDescription() : "—"));

        infoTable.addCell(createHeaderCell("Приоритет"));
        infoTable.addCell(createBodyCell(project.getPriority()));

        infoTable.addCell(createHeaderCell("Статус"));
        infoTable.addCell(createBodyCell(project.getStatus()));

        infoTable.addCell(createHeaderCell("Дедлайн"));
        infoTable.addCell(createBodyCell(project.getDeadline() != null ? project.getDeadline().toString() : "—"));

        infoTable.addCell(createHeaderCell("Цвет проекта"));
        infoTable.addCell(new Cell().add(new Paragraph(project.getColor()).setFont(font).setFontColor(color)));

        infoTable.addCell(createHeaderCell("Количество участников"));
        infoTable.addCell(createBodyCell(String.valueOf(project.getMembers().size())));

        infoTable.addCell(createHeaderCell("Количество ролей"));
        infoTable.addCell(createBodyCell(String.valueOf(project.getRoles().size())));

        infoTable.addCell(createHeaderCell("Количество тегов"));
        infoTable.addCell(createBodyCell(String.valueOf(project.getTags().size())));

        infoTable.addCell(createHeaderCell("Количество статусов"));
        infoTable.addCell(createBodyCell(String.valueOf(project.getStatuses().size())));

        doc.add(infoTable);

        doc.add(new Paragraph("\n\n"));
    }

    // Вспомогательные методы:
    private Cell createHeaderCell(String text) {
        return new Cell().add(new Paragraph(text))
                .setFontSize(11)
                .setFontColor(ColorConstants.GRAY)
                .setBold()
                .setBackgroundColor(new DeviceRgb(245, 245, 245))
                .setBorderBottom(new SolidBorder(1));
    }

    private Cell createBodyCell(String text) {
        return new Cell().add(new Paragraph(text))
                .setFontSize(11)
                .setBorderBottom(new SolidBorder(1));
    }

    private String formatPermissionName(String permission) {
        return switch (permission) {
            case "CAN_CREATE_TASKS" -> "Создание задач";
            case "CAN_EDIT_TASKS" -> "Редактирование задач";
            case "CAN_DELETE_TASKS" -> "Удаление задач";
            case "CAN_ARCHIVE_TASKS" -> "Архивирование задач";
            case "CAN_RESTORE_TASKS" -> "Восстановление задач";
            case "CAN_MANAGE_TASK_STATUSES" -> "Управление статусами";
            case "CAN_MANAGE_REMINDERS" -> "Управление напоминаниями";
            case "CAN_MANAGE_TASK_TAGS" -> "Управление тегами";
            case "CAN_MANAGE_ASSIGNEES" -> "Назначение участников";
            case "CAN_EDIT_PROJECT" -> "Редактирование проекта";
            case "CAN_MANAGE_MEMBERS" -> "Управление участниками";
            case "CAN_MANAGE_ROLES" -> "Управление ролями";
            case "CAN_VIEW_ANALYTICS" -> "Просмотр аналитики";
            case "CAN_INVITE" -> "Приглашение участников";
            default -> permission.replace("_", " ").toLowerCase(Locale.ROOT);
        };
    }

    private byte[] createPieChartWithColors(Map<String, Long> data, String title, Map<String, String> colorMap) throws Exception {
        DefaultPieDataset dataset = new DefaultPieDataset();
        data.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
        chart.setBackgroundPaint(Color.WHITE);
        chart.setAntiAlias(true); // улучшение качества
        // Заголовок
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 16));

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionOutlinesVisible(false);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setLabelBackgroundPaint(Color.WHITE);
        plot.setLabelOutlinePaint(null);
        plot.setLabelShadowPaint(null);
        // Увеличенный шрифт подписей секторов
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 20));

        // Увеличенный шрифт легенды
        if (chart.getLegend() != null) {
            chart.getLegend().setItemFont(new Font("SansSerif", Font.PLAIN, 20));
        }
        // Применяем кастомные цвета
        for (String key : data.keySet()) {
            String hex = colorMap.getOrDefault(key, "#9932CC"); // default фиолетовый
            plot.setSectionPaint(key, Color.decode(hex));
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(out, chart, 800, 600); // качество
        return out.toByteArray();
    }

    private byte[] createPieChart(Map<String, Long> data, String title) throws Exception {
        DefaultPieDataset dataset = new DefaultPieDataset();
        data.forEach(dataset::setValue);
        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionOutlinesVisible(false);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(out, chart, 500, 400);
        return out.toByteArray();
    }

    private byte[] createBarChart(Map<String, Long> data, String title, String categoryAxisLabel, String valueAxisLabel) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((k, v) -> dataset.addValue(v, "Количество", k));

        JFreeChart chart = ChartFactory.createBarChart(title, categoryAxisLabel, valueAxisLabel, dataset);

        // Повышаем качество
        chart.setBackgroundPaint(Color.WHITE);
        chart.setAntiAlias(true);
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 16));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setOutlineVisible(false);

        // Цветовая схема: ваш фиолетовый
        Color mainColor = Color.decode("#9932CC");

        // Убираем 3D-эффект, добавляем заливку
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setSeriesPaint(0, mainColor);
        renderer.setBarPainter(new StandardBarPainter()); // Убираем блики

        // Подписи
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));

        // Вывод в PNG
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(out, chart, 800, 500);
        return out.toByteArray();
    }

}
