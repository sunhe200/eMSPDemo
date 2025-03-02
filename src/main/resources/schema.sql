-- 删除旧表（如果存在）
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS card;
DROP TABLE IF EXISTS token;

-- 创建账户表
CREATE TABLE account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,             -- 账户的唯一标识
    status VARCHAR(50),                             -- 账户状态 (CREATED, ACTIVATED, DEACTIVATED)
    last_updated TIMESTAMP,                         -- 最后更新时间
    editor VARCHAR(50)
);

-- 创建卡片表
CREATE TABLE card (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(50),                             -- 卡片状态 (CREATED, ASSIGNED, ACTIVATED, DEACTIVATED)
    account_id BIGINT,                              -- 关联的账户ID（可为空）
    last_updated TIMESTAMP,                         -- 最后更新时间
    editor VARCHAR(50)
);

-- 创建token表
CREATE TABLE token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    external_id BIGINT,               -- 用于关联 Account 或 Card 表的记录 ID
    token_type VARCHAR(50) NOT NULL,  -- 'RFID' 或 'EMAID'
    prop_name VARCHAR(255) NOT NULL,  -- 属性名，如 RFID 类型的 "uid" 或 "visibleNumber"，EMAID 类型的 "contractId"
    prop_value VARCHAR(255)           -- 属性值
);