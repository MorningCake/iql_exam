package ru.iql.exam.exception.handler;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.iql.exam.exception.AlreadyExistsException;
import ru.iql.exam.exception.AuthenticationFailedException;
import ru.iql.exam.exception.PermissionDeniedException;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

/**
 * Handler for web exception
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
@Setter
public class RequestExceptionHandler extends ResponseEntityExceptionHandler {

    private MessageSource messageSource;

    private static final String DEFAULT_MESSAGE = "No message available";
    private static final String HEADER_TIMESTAMP = "timestamp";
    private static final String HEADER_STATUS = "status";
    private static final String HEADER_ERROR = "error";
    private static final String HEADER_MESSAGE = "message";
    private static final String HEADER_PATH = "path";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String HIB_CONSTRAINT_VIOLATION_EXCEPTION = "org.hibernate.exception.ConstraintViolationException";
    private static final Pattern DUPLICATE_VALUE_CONSTRAINT_PATTERN = Pattern.compile("Key \\((?<field>\\w+)\\)=\\((?<value>[\\w\\s]+)\\) already exists");

    @ExceptionHandler({ EntityNotFoundException.class, RuntimeException.class, IllegalArgumentException.class,
            AlreadyExistsException.class, AuthenticationFailedException.class, PermissionDeniedException.class})
    public ResponseEntity<Object> handleServiceException(Exception ex, WebRequest request) {
        var status = resolveAnnotatedResponseStatus(ex);
        return handleExceptionInternal(ex, getExceptionBody(ex, status, request),
                                       new HttpHeaders(), status, request);
    }
    
    @ExceptionHandler({ ResponseStatusException.class })
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        var status = ex.getStatus();
        return handleExceptionInternal(ex, getExceptionBody(ex,status,request), new HttpHeaders(),
                                       status, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException e, WebRequest request) {
        var status = HttpStatus.BAD_REQUEST;
        var bodyBuilder = ExceptionBody.builder().message(status.getReasonPhrase())
                                       .path(((ServletWebRequest) request).getRequest().getRequestURI());
    
        for (var violation : e.getConstraintViolations()) {
            var constraintType = violation.getConstraintDescriptor().getAnnotation().annotationType();
            if (constraintType.getPackageName().startsWith("ru.sberbank")) {
                return handleServiceException(e, request);
            }
            var constraint = Constraint.builder()
                                       .type(constraintType.getSimpleName())
                                       .build();
        
            var problem = Problem.builder()
                                 .field(violation.getPropertyPath().toString().replace("set.data", ""))
                                 .value(String.valueOf(violation.getInvalidValue()))
                                 .constraints(Collections.singletonList(constraint)).build();
        
            bodyBuilder.problem(problem);
        }
        
        return ResponseEntity.status(status).body(bodyBuilder.build());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(DataIntegrityViolationException e, WebRequest request) {
        var status = HttpStatus.CONFLICT;
        var bodyBuilder = ExceptionBody.builder().message(status.getReasonPhrase())
                .path(((ServletWebRequest) request).getRequest().getRequestURI());
        var problemBuilder = Problem.builder();
        switch (e.getCause().getClass().getCanonicalName()) {
            case (HIB_CONSTRAINT_VIOLATION_EXCEPTION): {
                var matcher = DUPLICATE_VALUE_CONSTRAINT_PATTERN.matcher(e.getMostSpecificCause().getMessage());
                while (matcher.find()) {
                    var constraint = Constraint.builder()
                            .type("Unique")
                            .build();

                    problemBuilder.field(matcher.group("field"))
                            .value(matcher.group("value"))
                            .constraints(Collections.singletonList(constraint));
                }
            }break;
            default: {
                var constraint = Constraint.builder()
                        .type(e.getMostSpecificCause().getMessage())
                        .build();

                problemBuilder
                        .constraints(Collections.singletonList(constraint));
            }
        }
        bodyBuilder.problem(problemBuilder.build());
        return ResponseEntity.status(status).body(bodyBuilder.build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionBody> handleMethodArgumentTypeNotValid(MethodArgumentTypeMismatchException e,
                                                                          @NonNull WebRequest request) {
        var status = HttpStatus.BAD_REQUEST;
        var bodyBuilder = ExceptionBody.builder().message(status.getReasonPhrase())
                                       .path(((ServletWebRequest) request).getRequest().getRequestURI());
    
        var requiredType = Optional.ofNullable(e.getRequiredType()).map(Class::getSimpleName).orElse(null);
        bodyBuilder.problem(Problem.builder().field(e.getName()).value(String.valueOf(e.getValue()))
                                   .constraints(Collections.singletonList(Constraint.builder().type("TypeRequired").value(requiredType)
                                                         .build())).build());
    
        return ResponseEntity.status(status).body(bodyBuilder.build());
    }

    @Override
    protected @NonNull
    ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            @NonNull HttpHeaders headers,
            HttpStatus status,
            @NonNull WebRequest request
    ) {
        var bodyBuilder = ExceptionBody.builder().message(status.getReasonPhrase())
                                       .path(((ServletWebRequest) request).getRequest().getRequestURI());
        
        var model = e.getBindingResult().getModel();
        var problems = new ArrayList<Problem>();
        for (var key : model.keySet()) {
            var value = model.get(key);
            if (value instanceof BeanPropertyBindingResult) {
                var result = (BeanPropertyBindingResult) value;
                for (var error : result.getAllErrors()) {
                    if (error instanceof FieldError) {
                        var fieldError = (FieldError) error;
                        var fieldName = fieldError.getField();
                        var problem =
                                problems.stream().filter(p -> fieldName.equals(p.getField())).findFirst()
                                        .orElse(Problem.builder()
                                               .field(fieldError.getField())
                                               .value(convertValue(fieldError.getRejectedValue(),
                                                                   fieldError.getDefaultMessage())).build());
                        problem.getConstraints().add(Constraint.builder()
                                                                   .type(error.getCode())
                                                                   .value(extractValues(error))
                                                                   .build());
                        problems.add(problem);
                    } else if (error instanceof ObjectError) {
                        var fieldsNamesBuilder = new StringBuilder();
                        for (var argument : Objects.requireNonNull(error.getArguments())) {
                            if (argument.getClass().getName().equals("org.springframework.validation.beanvalidation" +
                                                  ".SpringValidatorAdapter$ResolvableAttribute")) {
                                var fieldName = argument.toString();
                                fieldsNamesBuilder.append(fieldName).append(" - ");
                            }
                        }
                        var problem = Problem.builder()
                                             .field(fieldsNamesBuilder.substring(0, fieldsNamesBuilder.length() - 3))
                                             .value("")
                                             .constraints(Collections.singletonList(Constraint.builder()
                                                                   .type(error.getCode())
                                                                   .build())).build();
                        problems.add(problem);
                    }
                }
            }
        }
        bodyBuilder.problems(problems);
        return ResponseEntity.status(status).body(bodyBuilder.build());
    }
    
    @Override
    protected @NonNull
    ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            @NonNull HttpHeaders headers,
            HttpStatus status,
            @NonNull WebRequest request
                                                       ) {
        var message = ex.getMessage();
        
        var bodyBuilder = ExceptionBody.builder()
                                       .message(status.getReasonPhrase())
                                       .path(((ServletWebRequest) request).getRequest().getRequestURI());
        
        if (ex.getCause() instanceof InvalidFormatException) {
            bodyBuilder.problems(extractInvalidFormatProblems((InvalidFormatException) ex.getCause()));
        } else if (ex.getCause() instanceof JsonParseException) {
            bodyBuilder.message(ex.getCause().getMessage());
        } else if (message != null && message.contains("body is missing")) {
            bodyBuilder.message("Request body is missing");
        } else {
            bodyBuilder.message(message);
        }
        
        return ResponseEntity.status(status).body(bodyBuilder.build());
    }
    
    HttpStatus resolveAnnotatedResponseStatus(Exception exception) {
        ResponseStatus annotation = findMergedAnnotation(exception.getClass(), ResponseStatus.class);
        if (annotation != null) {
            return annotation.value();
        }
        if (exception instanceof RestClientResponseException) {
            return HttpStatus.valueOf(((RestClientResponseException) exception).getRawStatusCode());
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    
    @Override
    protected @NonNull
    ResponseEntity<Object> handleExceptionInternal(
            @NonNull Exception exception,
            Object body,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatus status,
            @NonNull WebRequest request
                                                  ) {
        var apiError = Objects.isNull(body) || "".equals(body)
                       ? getExceptionBody(exception, status, request)
                       : body;
        log.error(getExceptionBody(exception, status, request).toString(), exception);
        return super.handleExceptionInternal(exception, apiError, headers, status, request);
    }
    
    private Map<String, Object> getExceptionBody(Exception exception, HttpStatus status, WebRequest request) {
        var message = exception.getMessage();
        
        Map<String, Object> newBody = new LinkedHashMap<>();
        var currentTime = ZonedDateTime.now(ZoneId.of(ZoneOffset.UTC.getId()))
                                       .format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
        newBody.put(HEADER_TIMESTAMP, currentTime);
        newBody.put(HEADER_STATUS, status.value());
        newBody.put(HEADER_MESSAGE, message != null ? message : DEFAULT_MESSAGE);
        newBody.put(HEADER_ERROR, status.getReasonPhrase());
        newBody.put(HEADER_PATH, ((ServletWebRequest) request).getRequest().getRequestURI());
        return newBody;
    }
  
    private String extractPath(List<JsonMappingException.Reference> path) {
        var builder = new StringBuilder();
        for (var item : path) {
            var pathIndex = Optional.of(item.getIndex()).filter(i -> i >= 0).map(i -> String.format("[%s]", i))
                                    .orElse("");
            if (!builder.toString().isBlank() && !pathIndex.startsWith("[") && !pathIndex.endsWith("]")) {
                builder.append(".");
            }
            builder.append(Optional.ofNullable(item.getFieldName()).orElse(""));
            builder.append(pathIndex);
        }
        return builder.toString();
    }
    
    private Set<Problem> extractInvalidFormatProblems(InvalidFormatException ex) {
        var problems = new HashSet<Problem>();
        
        problems.add(Problem.builder()
                            .field(extractPath(ex.getPath()))
                            .value(String.valueOf(ex.getValue()))
                            .constraints(extractConstraintMap(ex.getTargetType()))
                            .build());
        
        return problems;
    }
    
    private List<Constraint> extractConstraintMap(Class<?> target) {
        var constraints = new ArrayList<Constraint>();
        if (Enum.class.isAssignableFrom(target)) {
            var constraint = Constraint.builder()
                                       .type("Enum")
                                       .value(Arrays.stream(target.getEnumConstants())
                                                    .map(e -> (Enum<?>) e).map(Enum::name)
                                                    .toArray()).build();
            constraints.add(constraint);
        }
        return constraints;
    }
    
    /**
     * Конвертирует отклоненное значение для ошибки.
     *
     * @param rejectedValue отклоненное сообщение.
     * @param defaultValue сообщение по умолчанию.
     * @return конвертированное сообщение.
     */
    private String convertValue(Object rejectedValue, String defaultValue) {
        if (rejectedValue == null) {
            return null;
        }
        if (rejectedValue instanceof String
            || rejectedValue instanceof Number
            || rejectedValue instanceof Boolean) {
            return String.valueOf(rejectedValue);
        }
        return rejectedValue.toString();
    }
    
    /**
     * Получает список ограничений из ошибок.
     *
     * @param error объект ошибки.
     * @return ограничения.
     */
    private Object extractValues(ObjectError error) {
        if (error != null) {
            if ("Size".equals(error.getCode())) {
                var arguments = error.getArguments();
                if (arguments == null) {
                    return null;
                }
                var map = new HashMap<String, Object>();
                map.put("minLength", arguments[2]);
                map.put("maxLength", arguments[1]);
                return map;
            }
        }
        return null;
    }

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
