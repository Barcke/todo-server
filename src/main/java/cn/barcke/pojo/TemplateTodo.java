package cn.barcke.pojo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author Barcke
 * @version 1.0
 * @projectName calendar-design
 * @className TemplateTodo
 * @date 2025/12/16
 * @slogan: 源于生活 高于生活
 * @description: 模板项实体类
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Entity
@Table(name = "template_todo")
public class TemplateTodo {

    /**
     * 模板Todo ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 64)
    private String id;

    /**
     * 所属模板ID
     */
    @Column(name = "template_id", nullable = false, length = 64)
    private String templateId;

    /**
     * Todo 内容
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * Todo 类型ID（可选）
     */
    @Column(name = "type_id", length = 64)
    private String typeId;

    /**
     * 排序
     */
    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;
}

