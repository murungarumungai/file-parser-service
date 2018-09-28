package entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "logs")
@Access(value= AccessType.FIELD)
public @Data class LogEntry implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int logId;

    @Column(name = "request_params")
    private String requestParams;

    @Column(name = "request_ip")
    private String requestIp;

    @Column(name = "performance_metrics")
    private String performanceMetrics;

}
