-- 学号字段迁移脚本
-- 为已有学生自动生成学号，然后添加唯一约束

-- 1. 为没有学号的学生生成学号（格式：S + 时间戳后6位 + 序号）
UPDATE students 
SET student_id = CONCAT('S', DATE_FORMAT(NOW(), '%y%m%d'), LPAD(id, 4, '0'))
WHERE student_id IS NULL OR student_id = '';

-- 2. 检查是否有重复学号（如果有，重新生成）
-- 重复学号会在这步报错，需要手动处理

-- 3. 添加唯一约束（如果不存在）
-- 注意：如果表已存在，JPA 可能不会自动添加约束
-- 手动执行：
ALTER TABLE students MODIFY student_id VARCHAR(20) NOT NULL;
ALTER TABLE students ADD UNIQUE INDEX idx_student_id (student_id);

-- 4. 验证
SELECT id, name, student_id FROM students;
