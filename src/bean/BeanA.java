package bean;

public class BeanA {

	private String name;
	private int num;
	private EnumA enumA;
	private BeanB beanB;
	private boolean isTrue;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public BeanB getBeanB() {
		return beanB;
	}
	public void setBeanB(BeanB beanB) {
		this.beanB = beanB;
	}
    public EnumA getEnumA() {
        return enumA;
    }
    public void setEnumA(EnumA enumA) {
        this.enumA = enumA;
    }
    public boolean isTrue() {
        return isTrue;
    }
    public void setTrue(boolean isTrue) {
        this.isTrue = isTrue;
    }
	
	
	
}
