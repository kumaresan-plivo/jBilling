/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2012] Enterprise jBilling Software Ltd.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Enterprise jBilling Software.
 * The intellectual and technical concepts contained
 * herein are proprietary to Enterprise jBilling Software
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden.
 */
package com.sapienter.jbilling.server.util.db;



import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@TableGenerator(
        name="language_GEN",
        table="jbilling_seqs",
        pkColumnName = "name",
        valueColumnName = "next_id",
        pkColumnValue="language",
        allocationSize = 10
)
@Table(name="language")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class LanguageDTO  implements Serializable {
     public static final int ENGLISH_LANGUAGE_ID = 1;

     private int id;
     private String code;
     private String description;
//     private Set<NotificationMessageDTO> notificationMessages = new HashSet<NotificationMessageDTO>(0);
//     private Set<CompanyDTO> entities = new HashSet<CompanyDTO>(0);
//     private Set<UserDTO> baseUsers = new HashSet<UserDTO>(0);

    public LanguageDTO() {
    }

    public LanguageDTO(int id) {
        this.id = id;
    }
    
    public LanguageDTO(int id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

//    public LanguageDTO(int id, String code, String description, Set<NotificationMessageDTO> notificationMessages, Set<CompanyDTO> entities, Set<UserDTO> baseUsers) {
//       this.id = id;
//       this.code = code;
//       this.description = description;
//       this.notificationMessages = notificationMessages;
//       this.entities = entities;
//       this.baseUsers = baseUsers;
//    }

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator = "language_GEN")
    @Column(name="id", unique=true, nullable=false)
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="code", nullable=false, length=2)
    public String getCode() {
        return this.code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    @Column(name="description", nullable=false, length=50)
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

//    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="language")
//    public Set<NotificationMessageDTO> getNotificationMessages() {
//        return this.notificationMessages;
//    }
//
//    public void setNotificationMessages(Set<NotificationMessageDTO> notificationMessages) {
//        this.notificationMessages = notificationMessages;
//    }

//    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="language")
//    public Set<CompanyDTO> getEntities() {
//        return this.entities;
//    }
//
//    public void setEntities(Set<CompanyDTO> entities) {
//        this.entities = entities;
//    }
//
//    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="language")
//    public Set<UserDTO> getBaseUsers() {
//        return this.baseUsers;
//    }
//
//    public void setBaseUsers(Set<UserDTO> baseUsers) {
//        this.baseUsers = baseUsers;
//    }

    public String getAuditKey(Serializable id) {
        return id.toString();
    }
}


