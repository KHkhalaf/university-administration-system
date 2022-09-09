package com.universityadministration.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ServiceListDto{
        private int id;
        private String studentName;
        private String employeeName;
        private String serviceType;
        private String serviceStatus;

        public ServiceListDto(int id, String studentName, String employeeName, String serviceType, String serviceStatus) {
                this.id = id;
                this.studentName = studentName;
                this.employeeName = employeeName;
                this.serviceType = serviceType;
                this.serviceStatus = serviceStatus;
        }

}
