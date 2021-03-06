package jp.co.h30.swdev.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jp.co.h30.swdev.bean.RegisterBean;
import jp.co.h30.swdev.dao.TodoDao;
import jp.co.h30.swdev.message.Messages;
import jp.co.h30.swdev.repository.TodoRepository;

public class RegisterServiceTest {
	private static final String DATE_FORMAT = "yyyyMMdd";
	private static final String DATE_FORMAT_WITH_SLASH = "yyyy/MM/dd";

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
	private static final DateFormat FORMAT = new SimpleDateFormat(DATE_FORMAT);

	@Mock
	private TodoRepository repository;

	@InjectMocks
	private RegisterService service;

	@BeforeEach
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@AfterEach
	public void tearDown() throws Exception {
	}

	@Test
	public void canRegisterWithAllColumns() {
		RegisterBean bean = new RegisterBean();
		bean.setTitle("Foo");
		bean.setDetail("Bar");
		LocalDate date = LocalDate.now();
		bean.setDeadline(date.format(FORMATTER));

		ArgumentCaptor<TodoDao> argument = ArgumentCaptor.forClass(TodoDao.class);

		boolean valid = service.execute(bean);

		assertTrue(valid);
		verify(repository).insert(argument.capture());

		TodoDao actualArgument = argument.getValue();
		assertNotNull(actualArgument.getId());
		assertEquals(bean.getTitle(), actualArgument.getTitle());
		assertEquals(bean.getDetail(), actualArgument.getDetail());
		assertEquals(date.format(FORMATTER), FORMAT.format(actualArgument.getDeadline()));
		assertNotNull(actualArgument.getCreatedDate());
	}

	@Test
	public void canRegisterWithSlashSeparatedDeadline() {
		RegisterBean bean = new RegisterBean();
		bean.setTitle("Foo");
		bean.setDetail("Bar");
		LocalDate date = LocalDate.now();
		bean.setDeadline(date.format(DateTimeFormatter.ofPattern(DATE_FORMAT_WITH_SLASH)));

		ArgumentCaptor<TodoDao> argument = ArgumentCaptor.forClass(TodoDao.class);
		boolean valid = service.execute(bean);

		assertTrue(valid);
		verify(repository).insert(argument.capture());

		TodoDao actualArgument = argument.getValue();
		assertNotNull(actualArgument.getId());
		assertEquals(bean.getTitle(), actualArgument.getTitle());
		assertEquals(bean.getDetail(), actualArgument.getDetail());
		assertEquals(date.format(FORMATTER), FORMAT.format(actualArgument.getDeadline()));
		assertNotNull(actualArgument.getCreatedDate());
	}

	@Test
	public void canRegisterWithNullDetailAndDeadline() {
		RegisterBean bean = new RegisterBean();
		bean.setTitle("Hoge");

		ArgumentCaptor<TodoDao> argument = ArgumentCaptor.forClass(TodoDao.class);
		boolean valid = service.execute(bean);

		assertTrue(valid);
		verify(repository).insert(argument.capture());

		TodoDao actualArgument = argument.getValue();
		assertNotNull(actualArgument.getId());
		assertEquals(bean.getTitle(), actualArgument.getTitle());
		assertNull(actualArgument.getDetail());
		assertNull(actualArgument.getDeadline());
		assertNotNull(actualArgument.getCreatedDate());
	}

	@Test
	public void canRegisterWithEmptyDetailAndDeadline() {
		RegisterBean bean = new RegisterBean();
		bean.setTitle("Hoge");
		bean.setDetail("");
		bean.setDeadline("");

		ArgumentCaptor<TodoDao> argument = ArgumentCaptor.forClass(TodoDao.class);
		boolean valid = service.execute(bean);

		assertTrue(valid);
		verify(repository).insert(argument.capture());

		TodoDao actualArgument = argument.getValue();
		assertNotNull(actualArgument.getId());
		assertEquals(bean.getTitle(), actualArgument.getTitle());
		assertEquals(bean.getDetail(), actualArgument.getDetail());
		assertNull(actualArgument.getDeadline());
		assertNotNull(actualArgument.getCreatedDate());
	}

	@Test
	public void failToRegisterDueToEmptyTitleAndUnparsableDeadline() throws IOException {
		RegisterBean bean = new RegisterBean();
		bean.setTitle("");
		bean.setDetail("");
		bean.setDeadline("Hoge");

		boolean result = service.execute(bean);

		assertFalse(result);

		List<String> messages = bean.getMessages();
		assertEquals(2, messages.size());
		for (String message : messages) {
			assertTrue(message.equals(Messages.getMessage("err.title"))
					|| message.equals(Messages.getMessage("err.deadline.format")));
		}
	}

	@Test
	public void failToRegisterDueToInvalidDate() {
		RegisterBean bean = new RegisterBean();
		bean.setTitle("Hoge");
		bean.setDetail("");
		bean.setDeadline("2018/10/32");

		boolean result = service.execute(bean);

		assertFalse(result);
		List<String> messages = bean.getMessages();
		assertEquals(Messages.getMessage("err.deadline.existence"),messages.get(0));
	}
	
	@Test
	public void failToRegisterDueToPastDate() {
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DATE, -1);
		
		RegisterBean bean = new RegisterBean();
		bean.setTitle("Hoge");
		bean.setDetail("");
		bean.setDeadline(FORMAT.format(today.getTime()));
		
		boolean result = service.execute(bean);
		
		assertFalse(result);
		List<String> messages = bean.getMessages();
		assertEquals(Messages.getMessage("err.deadline.past"),messages.get(0));
	}
	
	@Test
	public void failToRegisterDueToPastDateAgainstCriteriaDate() {
		Calendar criteriaDate = Calendar.getInstance();
		criteriaDate.add(Calendar.DATE, -1);
		System.setProperty("CRITERIA_DATE", new SimpleDateFormat(DATE_FORMAT_WITH_SLASH).format(criteriaDate.getTime()));
		
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DATE, -2);
		
		RegisterBean bean = new RegisterBean();
		bean.setTitle("Hoge");
		bean.setDetail("");
		bean.setDeadline(FORMAT.format(today.getTime()));
		
		boolean result = service.execute(bean);
		
		assertFalse(result);
		List<String> messages = bean.getMessages();
		assertEquals(Messages.getMessage("err.deadline.past"),messages.get(0));
	}
}
