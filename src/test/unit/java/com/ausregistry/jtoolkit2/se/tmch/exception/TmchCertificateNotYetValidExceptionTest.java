package com.ausregistry.jtoolkit2.se.tmch.exception;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import com.ausregistry.jtoolkit2.ErrorPkg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.security.cert.CertPathValidatorException;
import java.util.Date;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ErrorPkg.class})
public class TmchCertificateNotYetValidExceptionTest {
    @Test
    public void shouldReturnCorrectDate() {
        Date mockDate = mock(Date.class);
        assertThat(new TmchCertificateNotYetValidException(mockDate, null).getValidFromDate(), is(mockDate));
    }

    @Test
    public void shouldReturnCorrectMessage() {
        mockStatic(ErrorPkg.class);
        Date mockDate = mock(Date.class);
        when(ErrorPkg.getMessage("tmch.smd.cert.notYetValid", "<<valid-from-date>>", mockDate)).thenReturn("message");
        TmchCertificateNotYetValidException exception = new TmchCertificateNotYetValidException(mockDate, null);
        assertThat(exception.getMessage(), is("message"));

    }

    @Test
    public void shouldEncapsulateItsCause() {
        Throwable mockCause = mock(CertPathValidatorException.class);
        Throwable exception = new TmchCertificateNotYetValidException(null, (CertPathValidatorException) mockCause);
        assertThat(exception.getCause(), is(mockCause));
    }
}