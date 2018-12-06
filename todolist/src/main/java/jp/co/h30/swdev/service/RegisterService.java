package jp.co.h30.swdev.service;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import jp.co.h30.swdev.bean.RegisterBean;
import jp.co.h30.swdev.dao.TodoDao;
import jp.co.h30.swdev.repository.RepositoryFactory;
import jp.co.h30.swdev.repository.TodoRepository;

public class RegisterService {
	private static final String DATE_FORMAT = "yyyyMMdd";
	TodoRepository repository;
	
	public RegisterService() {
		repository = RepositoryFactory.getInstance().generateRepository();
	}
	
	/**
	 * @param bean �o�^����Todo�A�C�e���̏��
	 * @return Todo�A�C�e���̓o�^�ɐ���������{@code true}, ���s������{@code false}��ԋp���܂�.
	 */
	public boolean execute(RegisterBean bean) {
		TodoDao dao = new TodoDao();
		dao.setTitle(bean.getTitle());
		dao.setDetail(bean.getDetail());
		
		DateFormat format = new SimpleDateFormat(DATE_FORMAT);
		try {
			java.util.Date deadline = format.parse(bean.getDeadline());
			dao.setDeadline(new Date(deadline.getTime()));
		} catch (ParseException e) {
			bean.setMessage("�s���ȓ��t�t�H�[�}�b�g�ł�");
			return false;
		}
		dao.setCreatedDate(new Date(System.currentTimeMillis()));
		
		repository.insert(dao);
		
		return true;
	}
}
