package org.opencloudengine.garuda.web.oauth;

import com.cloudant.client.api.model.Response;
import com.cloudant.client.api.views.Key;
import com.cloudant.client.api.views.ViewRequestBuilder;
import com.cloudant.client.api.views.ViewResponse;
import org.mybatis.spring.SqlSessionTemplate;
import org.opencloudengine.garuda.backend.scheduler.jobs.StopJob;
import org.opencloudengine.garuda.common.repository.PersistentRepositoryImpl;
import org.opencloudengine.garuda.couchdb.CouchServiceFactory;
import org.opencloudengine.garuda.util.JsonUtils;
import org.opencloudengine.garuda.web.console.oauthclient.OauthClient;
import org.opencloudengine.garuda.web.management.Management;
import org.opencloudengine.garuda.web.management.ManagementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OauthTokenRepositoryImpl implements OauthTokenRepository {

    private String CODE_NAMESPACE = "oauth_code";
    private String TOKEN_NAMESPACE = "oauth_access_token";

    private Logger logger = LoggerFactory.getLogger(OauthTokenRepository.class);

    @Autowired
    CouchServiceFactory serviceFactory;

    @Override
    public OauthCode insertCode(OauthCode oauthCode) {
        long time = new Date().getTime();
        oauthCode.setDocType(CODE_NAMESPACE);
        oauthCode.setRegDate(time);
        oauthCode.setUpdDate(time);

        Response response = serviceFactory.getDb().save(oauthCode);
        oauthCode.set_id(response.getId());
        oauthCode.set_rev(response.getRev());
        return oauthCode;
    }

    @Override
    public OauthCode selectCodeById(String id) {
        try {
            ViewRequestBuilder builder = serviceFactory.getDb().getViewRequestBuilder(CODE_NAMESPACE, "selectCodeById");
            Key.ComplexKey complex = new Key().complex(id);
            return builder.newRequest(Key.Type.COMPLEX, OauthCode.class).
                    keys(complex).
                    build().getResponse().getRows().get(0).getValue();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public OauthCode selectCodeByCode(String code) {
        try {
            ViewRequestBuilder builder = serviceFactory.getDb().getViewRequestBuilder(CODE_NAMESPACE, "selectCodeByCode");
            Key.ComplexKey complex = new Key().complex(code);
            return builder.newRequest(Key.Type.COMPLEX, OauthCode.class).
                    keys(complex).
                    build().getResponse().getRows().get(0).getValue();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public OauthCode selectCodeByCodeAndClientId(String code, String clientId) {
        try {
            ViewRequestBuilder builder = serviceFactory.getDb().getViewRequestBuilder(CODE_NAMESPACE, "selectCodeByCodeAndClientId");
            Key.ComplexKey complex = new Key().complex(code).add(clientId);
            return builder.newRequest(Key.Type.COMPLEX, OauthCode.class).
                    keys(complex).
                    build().getResponse().getRows().get(0).getValue();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void deleteCodeById(String id) {
        OauthCode oauthCode = this.selectCodeById(id);
        serviceFactory.getDb().remove(oauthCode);
    }

    @Override
    public OauthAccessToken insertToken(OauthAccessToken oauthAccessToken) {
        long time = new Date().getTime();
        oauthAccessToken.setDocType(TOKEN_NAMESPACE);
        oauthAccessToken.setRegDate(time);
        oauthAccessToken.setUpdDate(time);

        Response response = serviceFactory.getDb().save(oauthAccessToken);
        oauthAccessToken.set_id(response.getId());
        oauthAccessToken.set_rev(response.getRev());
        return oauthAccessToken;
    }

    @Override
    public OauthAccessToken selectTokenById(String id) {
        try {
            ViewRequestBuilder builder = serviceFactory.getDb().getViewRequestBuilder(TOKEN_NAMESPACE, "selectTokenById");
            Key.ComplexKey complex = new Key().complex(id);
            return builder.newRequest(Key.Type.COMPLEX, OauthAccessToken.class).
                    keys(complex).
                    build().getResponse().getRows().get(0).getValue();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public OauthAccessToken selectTokenByToken(String token) {
        try {
            ViewRequestBuilder builder = serviceFactory.getDb().getViewRequestBuilder(TOKEN_NAMESPACE, "selectTokenByToken");
            Key.ComplexKey complex = new Key().complex(token);
            return builder.newRequest(Key.Type.COMPLEX, OauthAccessToken.class).
                    keys(complex).
                    build().getResponse().getRows().get(0).getValue();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public OauthAccessToken selectTokenByRefreshToken(String refreshToken) {
        try {
            ViewRequestBuilder builder = serviceFactory.getDb().getViewRequestBuilder(TOKEN_NAMESPACE, "selectTokenByRefreshToken");
            Key.ComplexKey complex = new Key().complex(refreshToken);
            return builder.newRequest(Key.Type.COMPLEX, OauthAccessToken.class).
                    keys(complex).
                    build().getResponse().getRows().get(0).getValue();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public OauthAccessToken selectTokenByManagementIdAndId(String managementId, String id) {
        try {
            ViewRequestBuilder builder = serviceFactory.getDb().getViewRequestBuilder(TOKEN_NAMESPACE, "selectTokenByManagementIdAndId");
            Key.ComplexKey complex = new Key().complex(managementId).add(id);
            return builder.newRequest(Key.Type.COMPLEX, OauthAccessToken.class).
                    keys(complex).
                    build().getResponse().getRows().get(0).getValue();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public OauthAccessToken updateTokenById(OauthAccessToken oauthAccessToken) {
        OauthAccessToken existToken = this.selectTokenById(oauthAccessToken.get_id());

        existToken = (OauthAccessToken) JsonUtils.merge(existToken, oauthAccessToken);
        long time = new Date().getTime();
        existToken.setUpdDate(time);

        Response update = serviceFactory.getDb().update(existToken);
        existToken.set_rev(update.getRev());
        return existToken;
    }

    @Override
    public void deleteExpiredToken(String clientId, Long expirationTime, String tokenType) {
        List<Map> list = new ArrayList<>();
        try {
            String viewName = tokenType.equals("JWT") ? "selectJwtExpiredToken" : "selectBearerExpiredToken";

            ViewRequestBuilder builder = serviceFactory.getDb().getViewRequestBuilder(TOKEN_NAMESPACE, viewName);
            Key.ComplexKey startKey = new Key().complex(clientId).add(0);
            Key.ComplexKey endKey = new Key().complex(clientId).add(expirationTime);
            List<ViewResponse.Row<Key.ComplexKey, Map>> rows = builder.newRequest(Key.Type.COMPLEX, Map.class).
                    limit(100).skip(0).//descending(true).
                    startKey(startKey).endKey(endKey).
                    build().getResponse().getRows();

            for (ViewResponse.Row<Key.ComplexKey, Map> row : rows) {
                Map value = row.getValue();
                Map toDelete = new HashMap();
                toDelete.put("_id", value.get("_id"));
                toDelete.put("_rev", value.get("_rev"));
                toDelete.put("_deleted", true);
                list.add(toDelete);
            }


            if (list.size() > 0) {
                serviceFactory.getDb().bulk(list);
            }

            logger.info("{} tokens deleted, {}", list.size(), viewName);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void deleteTokenById(String id) {
        OauthAccessToken accessToken = this.selectTokenById(id);
        serviceFactory.getDb().remove(accessToken);
    }
}
