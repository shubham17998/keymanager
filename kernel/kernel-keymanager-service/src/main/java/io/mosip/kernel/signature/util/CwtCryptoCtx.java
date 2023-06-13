package io.mosip.kernel.signature.util;

import COSE.MessageTag;
import COSE.OneKey;
import COSE.Recipient;
import COSE.Signer;
import com.upokecenter.cbor.CBORObject;

import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class holds the singing/mac-ing/encrypting context for a CWT or COSE message.
 * 
 * Instead of a constructor, use the static methods, which give you an indication
 * what parameters are expected for the different COSE message types.
 * 
 * @author Dhanendra
 *
 */
public class CwtCryptoCtx {

	static {
        Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
	
    /**
     * What kind of COSE message does this context allow to create
     */
    private MessageTag what;
    
	private List<Signer> signers = Collections.emptyList();
	
	private OneKey privatekey = null;
	
	private List<Recipient> recipients = Collections.emptyList();
	
	private byte[] rawSymmetricKey = null;

	private OneKey publicKey = null;
	
	private CBORObject alg = null;
	
	protected CwtCryptoCtx(MessageTag what, byte[] key, CBORObject alg) {
	    this.what = what;
		this.rawSymmetricKey = key;
		this.alg = alg;
	}
	
	protected CwtCryptoCtx(MessageTag what, OneKey publicKey,
                           OneKey privateKey, CBORObject alg) {
	    this.what = what;
		this.publicKey = publicKey;
		this.privatekey = privateKey;
		this.alg = alg;
	}

	protected CwtCryptoCtx(MessageTag what, List<Recipient> recipients,
                           CBORObject alg) {
	    this.what = what;
		this.recipients = new ArrayList<>();
		this.recipients.addAll(recipients);
		this.alg = alg;
	}

	
	protected CwtCryptoCtx(MessageTag what, List<Signer> signers,
                           CBORObject alg, boolean sign) {
	    this.what = what;
		this.signers = new ArrayList<>();
		this.signers.addAll(signers);
		this.alg = alg;
		if (sign) {//Do nothing, this is needed so this constructor will 
			//not be shadowed by the Recipients constructor
		}
	}

	/**
	 * Creates a context for making or verifying Encrypt COSE messages.
	 * 
	 * @param recipients  the list of recipients.
	 * @param alg  the encryption algorithm (from AlgorithmID.*.ASCBOR())
	 * @return  the matching context
	 */	
	public static CwtCryptoCtx encrypt(List<Recipient> recipients, CBORObject alg) {
		return new CwtCryptoCtx(MessageTag.Encrypt, recipients, alg);
	}

	/**
	 * Creates a context for encrypting and decrypting Encrypt0 COSE messages.
	 * 
	 * @param rawSymmetrickey  the raw symmetric key
	 * @param alg  the encryption algorithm (from AlgorithmID.*.ASCBOR())
	 * 
	 * @return  the matching context
	 */
	public static CwtCryptoCtx encrypt0(byte[] rawSymmetrickey, CBORObject alg) {
		return new CwtCryptoCtx(MessageTag.Encrypt0, rawSymmetrickey, alg);
	}
	
	/**
	 * Creates a context for making or verifying MAC COSE messages.
	 * 
	 * @param recipients  the list of recipients.
	 * @param alg  the mac algorithm (from AlgorithmID.*.ASCBOR())
	 * @return  the matching context
	 */
	public static CwtCryptoCtx mac(List<Recipient> recipients, CBORObject alg) {
		return new CwtCryptoCtx(MessageTag.MAC, recipients, alg);
	}
	
	/**
	 * Creates a context for making or verifying MAC0 COSE messages.
	 * 
	 * @param rawSymmetricKey  the raw symmetric key
	 * @param alg  the algorithm
	 * 
	 * @return  the matching context
	 */
	public static CwtCryptoCtx mac0(byte[] rawSymmetricKey, CBORObject alg) {
		return new CwtCryptoCtx(MessageTag.MAC0, rawSymmetricKey, alg);
	}
	
	/**
	 * Create a context for making Sign COSE messages.
	 * 
	 * @param signers  the signers
	 * @param alg  the signature algorithm (from AlgorithmID.*.ASCBOR())
	 * @return  the matching context
	 */
	public static CwtCryptoCtx signCreate(List<Signer> signers, CBORObject alg) {
		return new CwtCryptoCtx(MessageTag.Sign, signers, alg, true);
	}
	
	
	/**
	 * Create a context for verifying Sign COSE Messages.
	 * 
	 * @param publicKey  the public key to verify the signature
	 * @param alg   the signature algorithm (from AlgorithmID.*.ASCBOR())
	 * @return  the matching context
	 */
	public static CwtCryptoCtx signVerify(OneKey publicKey, CBORObject alg) {
		return new CwtCryptoCtx(MessageTag.Sign, publicKey, null, alg);
	}
	
	/**
	 * Creates a context for verifying Sign1 COSE messages.
	 * 
	 * @param publicKey  the public key of the signer
	 * @param alg  the signing algorithm (from  AlgorithmID.*.ASCBOR())
	 * 
	 * @return  the matching context
	 */
	public static CwtCryptoCtx sign1Verify(OneKey publicKey, CBORObject alg) {
			return new CwtCryptoCtx(MessageTag.Sign1, publicKey, null, alg);
	}
	
	/**
	 * Creates a context for signing Sign1 COSE messages.
	 * 
	 * @param privateKey  the private key of the signer
	 * @param alg  the signing algorithm (from  AlgorithmID.*.ASCBOR())
	 * 
	 * @return  the matching context
	 */
	public static CwtCryptoCtx sign1Create(OneKey privateKey, CBORObject alg) {
		return new CwtCryptoCtx(MessageTag.Sign1, null, privateKey, alg);
	}
		
	/**
	 * @return  the signers
	 */
	public List<Signer> getSigners() {
		return this.signers;
	}
	
	/**
	 * @return  the private key
	 */
	public OneKey getPrivateKey() {
		return this.privatekey;
	}
	
	/**
	 * @return  the algorithm
	 */
	public CBORObject getAlg() {
		return this.alg;
	}

	/**
	 * @return  the symmetric key
	 */
	public byte[] getKey() {
		return this.rawSymmetricKey;
	}
	
	/**
	 * @return  the recipients
	 */
	public List<Recipient> getRecipients() {
		return this.recipients;
	}

	/**
	 * @return  the public key
	 */
	public OneKey getPublicKey() {
		return this.publicKey;
	}
	
	/**
	 * @return  the message type
	 */
	public MessageTag getMessageType() {
	    return this.what;
	}
}   