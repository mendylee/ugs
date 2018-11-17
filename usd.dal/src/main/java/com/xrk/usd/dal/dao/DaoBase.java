package com.xrk.usd.dal.dao;

import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xrk.usd.dal.DalService;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 数据操作基础类，实现常见的数据库实体操作方法
 * DaoBase: DaoBase.java.
 * <p>
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年9月10日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public abstract class DaoBase<T extends java.io.Serializable> {
    protected Class<T> clazz;
    protected Logger logger;
    protected static EntityManagerFactory factory = null;

    public static void setFactory(EntityManagerFactory ef) {
        factory = ef;
    }

    public DaoBase() {
        doGetClass();
        logger = LoggerFactory.getLogger(this.getClass());

//		try {
//			this.factory = factory;
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			e.printStackTrace();
//		}
    }

    private void doGetClass() {
        Type genType = this.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        this.clazz = (Class<T>) params[0];
    }

    /**
     * 执行一个数据处理对象
     *
     * @param procObj
     * @return
     */
    protected int execute(DaoProcessObj procObj) {
        List<DaoProcessObj> lsProcObj = new ArrayList<DaoProcessObj>();
        lsProcObj.add(procObj);
        return execute(lsProcObj);
    }

    /**
     * 在一个事务中执行多个数据处理对象
     *
     * @param lsProcObj
     * @return
     */
    protected int execute(List<DaoProcessObj> lsProcObj) {
        int bRtn = 0;
        if (lsProcObj.size() < 1) {
            return bRtn;
        }

        EntityManager entityManager = null;
        try {
            entityManager = factory.createEntityManager();
        } catch (Exception e) {
            return bRtn;
        }
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            for (DaoProcessObj procObj : lsProcObj) {
                Query query = entityManager.createQuery(procObj.getHsql());
                if (procObj.isMapParams()) {
                    Map<String, Object> dictParams = procObj.getMapParams();
                    for (Entry<String, Object> kv : dictParams.entrySet()) {
                        query = query.setParameter(kv.getKey(), kv.getValue());
                    }
                } else {
                    List<Object> lsParams = procObj.getParams();
                    int position = 0;
                    for (Object obj : lsParams) {
                        query = query.setParameter(position++, obj);
                    }
                }
                bRtn = query.executeUpdate();
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error(e.getMessage(), e);
            DalService.onError(e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return bRtn;
    }

    public T getSingleResult(DaoProcessObj procObj) {
        EntityManager entityManager = factory.createEntityManager();
        try {
            List<T> list = this.setParamters(entityManager.createQuery(procObj.getHsql(), this.clazz), procObj).setMaxResults(1).getResultList();
            return (null==list||list.isEmpty())?null:list.get(0);
        }       
        catch (Exception e) {
        	logger.error(e.getMessage(), e);
        	DalService.onError(e);
        	throw e;
        }
        finally {
            entityManager.close();
        }
    }

    public int getCount(DaoProcessObj procObj){
        int ret = 0;
        EntityManager entityManager;
        try {
            entityManager = factory.createEntityManager();
        } catch (Exception e) {
            return ret;
        }
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Query query = entityManager.createQuery(procObj.getHsql());
            if (procObj.isMapParams()) {
                Map<String, Object> dictParams = procObj.getMapParams();

                for (Entry<String, Object> kv : dictParams.entrySet()) {
                    query = query.setParameter(kv.getKey(), kv.getValue());
                }
            } else {
                List<Object> lsParams = procObj.getParams();
                int position = 0;

                for (Object obj : lsParams) {
                    query = query.setParameter(position++, obj);
                }
            }
            Object obj = query.getSingleResult();
            ret = null==obj?0:Integer.parseInt(obj.toString());
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error(e.getMessage(), e);
            DalService.onError(e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return ret;
    }

    protected <Q extends Query> Q setParamters(Q query, DaoProcessObj procObj) {
        if (procObj.isMapParams()) {
            Map<String, Object> dictParams = procObj.getMapParams();

            for (Entry<String, Object> kv : dictParams.entrySet()) {
                query = (Q) query.setParameter(kv.getKey(), kv.getValue());
            }
        } else {
            List<Object> lsParams = procObj.getParams();
            int position = 0;

            for (Object obj : lsParams) {
                query = (Q) query.setParameter(position++, obj);
            }
        }

        return query;
    }

    public List<T> query(DaoProcessObj procObj, int pageSize, int pageIndex) {
        EntityManager entityManager = factory.createEntityManager();

        try {
            return this.setParamters(entityManager.createQuery(procObj.getHsql(), this.clazz), procObj)
                    .setFirstResult((pageIndex - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();
        }catch (Exception e) {
        	logger.error(e.getMessage(), e);
        	DalService.onError(e);
        	throw e;
        } finally {
            entityManager.close();
        }
    }

    /**
     * 执行一个数据查询操作
     *
     * @param procObj
     * @return
     */
    protected List<T> query(DaoProcessObj procObj) {
        List<T> lsRtn = null;
        EntityManager entityManager;
        try {
            entityManager = factory.createEntityManager();
        } catch (Exception e) {
            return lsRtn;
        }
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            TypedQuery<T> query = entityManager.createQuery(procObj.getHsql(), this.clazz);
            if (procObj.isMapParams()) {
                Map<String, Object> dictParams = procObj.getMapParams();

                for (Entry<String, Object> kv : dictParams.entrySet()) {
                    query = query.setParameter(kv.getKey(), kv.getValue());
                }
            } else {
                List<Object> lsParams = procObj.getParams();
                int position = 0;

                for (Object obj : lsParams) {
                    query = query.setParameter(position++, obj);
                }
            }
            lsRtn = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error(e.getMessage(), e);
            DalService.onError(e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return lsRtn;
    }

    /**
     * 检查指定的对象是否存在
     *
     * @param entity
     * @return
     */
    protected boolean contains(T entity) {
        EntityManager entityManager = null;
        boolean bRtn = true;
        try {
            entityManager = factory.createEntityManager();
            bRtn = entityManager.contains(entity);
        } catch (Exception e) {
            bRtn = false;
            logger.error(e.getMessage(), e);
            DalService.onError(e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return bRtn;
    }

    /**
     * 添加一个实体对象
     *
     * @param entity
     * @return
     */
    public boolean persist(Object entity) {
        boolean bRtn = true;
        EntityManager entityManager = null;
        try {
            entityManager = factory.createEntityManager();
        } catch (Exception e) {
            return false;
        }
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            bRtn = false;
            logger.error(e.getMessage(), e);
            DalService.onError(e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return bRtn;
    }

    /**
     * 添加一系列对象或多个对象
     *
     * @param lsEntity
     * @return
     */
    public boolean persist(List<T> lsEntity) {
        boolean bRtn = true;
        EntityManager entityManager = null;
        try {
            entityManager = factory.createEntityManager();
        } catch (Exception e) {
            return false;
        }
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            for (Object entity : lsEntity) {
                entityManager.persist(entity);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            bRtn = false;
            logger.error(e.getMessage(), e);
            DalService.onError(e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }

        return bRtn;
    }


    /**
     * @param entity
     * @return
     */
    public boolean merge(Object entity) {
        boolean bRtn = true;
        EntityManager entityManager = null;
        try {
            entityManager = factory.createEntityManager();
        } catch (Exception e) {
            return false;
        }
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            bRtn = false;
            logger.error(e.getMessage(), e);
            DalService.onError(e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }

        return bRtn;
    }

    /**
     * 移除指定的队列
     *
     * @param entity
     * @return
     */
    public boolean remove(Object entity) {
    	List<Object> lsEntity = new ArrayList<Object>();
    	lsEntity.add(entity);
    	return remove(lsEntity);
    }

    /**
     * 移除指定队列的实体对象
     *
     * @param lsEntity
     * @return
     */
    public boolean removeList(List<T> lsEntity) {
        boolean bRtn = true;
        EntityManager entityManager = null;
        try {
            entityManager = factory.createEntityManager();
        } catch (Exception e) {
            return false;
        }
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            for (Object entity : lsEntity) {
                entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            bRtn = false;
            logger.error(e.getMessage(), e);
            DalService.onError(e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }

        return bRtn;
    }

    /**
     * 根据主键ID查找记录
     *
     * @param entityClass
     * @param primaryKey
     * @return
     */
    protected T findById(Class<T> entityClass, Object primaryKey) {
        EntityManager entityManager = null;
        try {
            entityManager = factory.createEntityManager();
            return entityManager.find(entityClass, primaryKey);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            DalService.onError(e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return null;
    }

    public T findById(Object primaryKey) {
        return this.findById(clazz, primaryKey);
    }

    /**
     * 查询所有的记录
     *
     * @param entityClass
     * @return
     */
    protected List<T> findAll(Class<T> entityClass) {
        EntityManager entityManager = null;
        try {
            entityManager = factory.createEntityManager();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<T> query = builder.createQuery(entityClass);
            Root<T> variableRoot = query.from(entityClass);
            query.select(variableRoot);

            return entityManager.createQuery(query).getResultList();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            DalService.onError(e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return null;
    }

    public List<T> findAll() {
        return this.findAll(clazz);
    }

    protected List<T> findByPage(Integer pageIndex, Integer pageSize) {
        EntityManager entityManager = null;
        try {
            entityManager = factory.createEntityManager();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<T> query = builder.createQuery(clazz);
            Root<T> variableRoot = query.from(clazz);
            query.select(variableRoot);
            return entityManager.createQuery(query).setFirstResult((pageIndex - 1) * pageSize).setMaxResults(pageSize).getResultList();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            DalService.onError(e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return null;
    }


}
