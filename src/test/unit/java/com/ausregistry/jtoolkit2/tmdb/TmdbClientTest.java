package com.ausregistry.jtoolkit2.tmdb;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import com.ausregistry.jtoolkit2.session.KeyStoreNotFoundException;
import com.ausregistry.jtoolkit2.session.KeyStoreReadException;
import com.ausregistry.jtoolkit2.session.TLSContext;
import com.ausregistry.jtoolkit2.tmdb.model.TmNotice;
import com.ausregistry.jtoolkit2.tmdb.xml.TmNoticeXmlParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TmdbClient.class)
public class TmdbClientTest {

    @Mock private TmdbClientProperties mockTmdbClientProperties;
    @Mock private TLSContext mockTlsContext;
    @Mock private HttpsURLConnection mockConnection;
    @Mock private TmNoticeXmlParser mockTmNoticeXmlParser;
    @Mock private TmNotice mockTmNotice;

    private TmdbClient tmdbClient;
    private InputStream stringInputStream;

    @Before
    public void setUp() throws Exception {
        when(mockTmdbClientProperties.getTmdbServerUrl()).thenReturn("TMDB server URL");
        when(mockTmdbClientProperties.getTrustStoreFilename()).thenReturn("trustStoreFilename");
        when(mockTmdbClientProperties.getTrustStorePassphrase()).thenReturn("trustStorePassphrase");
        when(mockTmdbClientProperties.getTmdbSocketTimeOut()).thenReturn(1000);

        whenNew(TLSContext.class).withArguments("trustStoreFilename", "trustStorePassphrase")
                .thenReturn(mockTlsContext);

        when(mockTlsContext.createHttpsUrlConnection("TMDB server URL/lookupKey.xml", 1000)).thenReturn(mockConnection);

        stringInputStream = spy(new ByteArrayInputStream("noticeXml".getBytes()));
        when(mockConnection.getInputStream()).thenReturn(stringInputStream);

        whenNew(TmNoticeXmlParser.class).withNoArguments().thenReturn(mockTmNoticeXmlParser);
        when(mockTmNoticeXmlParser.parse("noticeXml")).thenReturn(mockTmNotice);
        whenNew(TmdbClientProperties.class).withArguments("tmdb.properties").thenReturn(mockTmdbClientProperties);

        tmdbClient = new TmdbClient();

    }

    @Test
    public void shouldCreateTlsContextWithTheRightParametersAndRequestAndParseNotice() throws Exception {
        TmNotice tmNotice = tmdbClient.requestNotice("lookupKey");
        assertThat(tmNotice, is(mockTmNotice));
    }

    @Test
    public void shouldCloseSocket() throws Exception {
        tmdbClient.requestNotice("lookupKey");

        verify(stringInputStream).close();
    }
}
